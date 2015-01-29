package ch.shaktipat.saraswati.internal.instrument.method;

import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_CURRENT_PFRAME_OF_CURRENT_PROCESS_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_EXECUTION_STATE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_INVOKED_METHOD_NAME_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_IS_CONTAINER_INVOCATION_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_PPROCESS_PMETHOD_COMMUNICATION_AREA_OF_CURRENT_PROCESS_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_PROGRAM_COUNTER_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_STACK_TRACE_ELEMENT_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.INIT_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_IS_CONTAINER_INVOCATION_METHOD;
import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.getJVMClassName;
import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.getJVMMethodDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javassist.CtMethod;
import javassist.bytecode.ByteArray;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import ch.shaktipat.exwrapper.javassist.bytecode.CodeIteratorWrapper;
import ch.shaktipat.saraswati.internal.common.DebugConstants;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.ExecutionState;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PFrame;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessPMethodCommunicationArea;

public class BytecodeAdjuster
{
	private static final Logger logger = Logger.getLogger( BytecodeAdjuster.class.getName() );

	private MethodInfo methodInfo;
	private CodeAttribute codeAttribute;
	private LocalVariables localVariables;
	private ConstPool constPool;
	
	public BytecodeAdjuster( CtMethod ctMethod )
	{
		methodInfo = ctMethod.getMethodInfo();
		codeAttribute = methodInfo.getCodeAttribute();
		constPool = methodInfo.getConstPool();
	}

	public void adjustByteCode( GenerateCodeResult generateCodeResult, List< MethodInvocation > methodInvocations, LocalVariables localVariables )
	{
		this.localVariables = localVariables;

		// Make adjustments
		adjustIndexesAndInsertInstrumentatedCode( generateCodeResult, methodInvocations );
		adjustForPreamble( generateCodeResult );

		// Optionally: output the mnemonics
		if ( DebugConstants.CONTAINER_INSTRUMENT_BYTECODE_ADJUSTER_DEBUG )
			InstrumentationLoggingHelper.outputMethod( logger, methodInfo );

		// Make final adjustments
		replaceJumps( generateCodeResult );
		adjustExceptionTable( generateCodeResult );

		if ( DebugConstants.CONTAINER_INSTRUMENT_LINE_NUMBERS_AS_BYTECODE_INDEXES_DEBUG )
			adjustLineNumbersForDebugging();
		
		// TBD: add an exception block around all instrumented code and
		// point it to a handler that throws an InternalException if
		// an exception occurs. This allows the framework to distinguish
		// between "normal" exceptions (i.e. those that need to be
		// delegated to the calling method / stack frame) and internal
		// ones due to instrumentation failure.
	}
	
	private void adjustLineNumbersForDebugging()
	{
		// Count the number of opcodes.
		int numOpCodes = 0;
		CodeIterator codeIterator = methodInfo.getCodeAttribute().iterator();
		
		while ( codeIterator.hasNext() )
		{
			++numOpCodes;
			CodeIteratorWrapper.next( codeIterator );
		}
		
		// Create new line number table.
		final int LINE_NUMBER_TABLE_LENGTH_SIZE = 2;
		final int START_PC_SIZE = 2;
		final int LINE_NUMBER_SIZE = 2;
		final int LINE_NUMBER_TABLE_ENTRY_SIZE = START_PC_SIZE + LINE_NUMBER_SIZE;
		byte[] lineNumberTable = new byte[ LINE_NUMBER_TABLE_LENGTH_SIZE + numOpCodes * LINE_NUMBER_TABLE_ENTRY_SIZE ];
		
		ByteArray.write16bit( numOpCodes, lineNumberTable, 0 );
		
		codeIterator.begin();
		int tablePos = LINE_NUMBER_TABLE_LENGTH_SIZE;
		while ( codeIterator.hasNext() )
		{
			int instructionIndex = CodeIteratorWrapper.next( codeIterator );
			ByteArray.write16bit( instructionIndex, lineNumberTable, tablePos );
			ByteArray.write16bit( instructionIndex, lineNumberTable, tablePos + START_PC_SIZE );
			tablePos += LINE_NUMBER_TABLE_ENTRY_SIZE;
		}
		
		LineNumberAttribute lna = (LineNumberAttribute)methodInfo.getCodeAttribute().getAttribute( LineNumberAttribute.tag );
		lna.set( lineNumberTable );
	}

	private void adjustIndexesAndInsertInstrumentatedCode( GenerateCodeResult generateCodeResult, List< MethodInvocation > methodInvocations )
	{
		assert ( generateCodeResult.getBytecodeInsertions().size() == generateCodeResult.getBranchEntries().size() );
		assert ( generateCodeResult.getBytecodeInsertions().size() == methodInvocations.size() );

		List< BytecodeInsertion > bytecodeInsertions = generateCodeResult.getBytecodeInsertions();
		List< BranchEntry > branchEntries = generateCodeResult.getBranchEntries();
		int insertionOffset = 0;

		CodeIterator codeIterator = codeAttribute.iterator();
		for ( int i = 0; i < bytecodeInsertions.size(); ++i )
		{
			BytecodeInsertion bytecodeInsertion = bytecodeInsertions.get( i );
			int insertionPoint = insertionOffset + bytecodeInsertion.getInsertionIndex();

			// delete the invokeX opcode
			byte[] byteCodeBeforeInsert = codeAttribute.getCode();
			byteCodeBeforeInsert[ insertionPoint ] = Opcode.NOP;
			byteCodeBeforeInsert[ insertionPoint + 1 ] = Opcode.NOP;
			byteCodeBeforeInsert[ insertionPoint + 2 ] = Opcode.NOP;

			// delete the "count" operand that occurs as part of invokeinterface
			if ( methodInvocations.get( i ).getMethodInvocationType().equals( MethodInvocationType.INVOKE_INTERFACE ) )
			{
				byteCodeBeforeInsert[ insertionPoint + 3 ] = Opcode.NOP;
			}

			// insert byte code
			byte[] byteCode = bytecodeInsertion.getBytecode().get();
			codeIterator.setMark( insertionPoint );
			CodeIteratorWrapper.insertEx( codeIterator, insertionPoint, byteCode );
			int insertionPointAfter = codeIterator.getMark();
			
			// Adjust start indexes if necessary.
			adjustStartIndexOfExceptionTableEntries( insertionPoint, insertionPointAfter );

			// note: due to the fact that javassist may insert NOPs to ensure
			// that LOOKUPSWITCH remains 4-byte aligned, the actual size of
			// inserted code may be greater than byteCode.length
			int actualBytecodeLength = insertionPointAfter - insertionPoint;

			// adjust the branch entries
			adjustIndexes( branchEntries, i, insertionPoint );

			// adjust the offset
			insertionOffset += actualBytecodeLength;
		}
	}
	
	// Note: When the instrumented bytecode is inserted with the insertEx() method, javassist automatically
	// adjusts the exception table. Unfortunately, if the insertion point happens to be the same as 
	// an exception handler's "from" or "to" index, that index is adjusted to point to *after* the insertion.
	//	The behaviour we require, however, is for the "from" and "to" index to remain the same in these cases.
	private void adjustStartIndexOfExceptionTableEntries( int insertionPointBefore, int insertionPointAfter )
	{
		ExceptionTable exceptionTable = codeAttribute.getExceptionTable();
		List< ExceptionTableEntry > exceptionTableEntries = ExceptionTableEntry.convert( exceptionTable );

		for ( ExceptionTableEntry entry : exceptionTableEntries )
		{
			if ( entry.getStartPc() == insertionPointAfter )
				entry.setStartPc( insertionPointBefore );
			if ( entry.getEndPc() == insertionPointAfter )
				entry.setEndPc( insertionPointBefore );
		}
		
		ExceptionTableEntry.replaceExceptionTable( exceptionTable, exceptionTableEntries );
	}

	private void adjustIndexes( List< BranchEntry > branchEntries, int entryNum, int indexOffset )
	{
		// Note that we are not touching branchInstructionIndex or
		// getPcInstructionIndex
		// since they occur within the preamble byte code
		BranchEntry branchEntry = branchEntries.get( entryNum );
		branchEntry.setBranchTargetIndex( branchEntry.getBranchTargetIndex() + indexOffset );
		branchEntry.setSetPcInstructionIndex( branchEntry.getSetPcInstructionIndex() + indexOffset );
		branchEntry.setThrownExceptionIndex( branchEntry.getThrownExceptionIndex() + indexOffset );
		branchEntry.setBeginInsertionIndex( branchEntry.getBeginInsertionIndex() + indexOffset );
		branchEntry.setEndInsertionIndex( branchEntry.getEndInsertionIndex() + indexOffset );
	}

	private void adjustForPreamble( GenerateCodeResult generateCodeResult )
	{
		List< BranchEntry > branchEntries = generateCodeResult.getBranchEntries();

		Bytecode preambleBytecode = getPreambleBytecode( branchEntries );
		byte[] preambleBcArray = preambleBytecode.get();

		CodeIterator codeIterator = codeAttribute.iterator();

		// We use setMark() / getMark() since the actual length of the
		// inserted bytecode may change depending on adjustments the framework
		// has to make (see javassist docs for CodeIterator.insert() )
		codeIterator.setMark( 0 );
		CodeIteratorWrapper.insertEx( codeIterator, 0, preambleBcArray );
		int preambleBcLength = codeIterator.getMark();

		// In cases where one or more exception handlers starts at byte code
		// zero
		// --> we need to adjust these to account for the preamble.
		adjustExceptionTableCaseHandlerStartsAtZero( preambleBcLength );

		// final adjustment on the branch entries to account for
		// the preamble
		for ( int i = 0; i < branchEntries.size(); ++i )
		{
			adjustIndexes( branchEntries, i, preambleBcLength );
		}
	}

	private void adjustExceptionTableCaseHandlerStartsAtZero( int preambleBcLength )
	{
		ExceptionTable exceptionTable = codeAttribute.getExceptionTable();

		for ( int i = 0; i < exceptionTable.size(); ++i )
		{
			if ( exceptionTable.startPc( i ) == 0 )
			{
				exceptionTable.setStartPc( i, preambleBcLength - 1 );
			}
		}
	}

	private Bytecode getPreambleBytecode( List< BranchEntry > branchEntries )
	{
		// Note: bytescodes cb-fd (203-253) are not currently assigned to
		// opcodes
		Bytecode bytecode = new Bytecode( constPool );

		InstrumentationHelper.emitComment( bytecode, constPool, "getPreambleBytecode() - begin" );

		// store vFrame ref in designated local variable
		bytecode.addInvokestatic( getJVMClassName( PersistentProcessImpl.class ), GET_PPROCESS_PMETHOD_COMMUNICATION_AREA_OF_CURRENT_PROCESS_METHOD, getJVMMethodDescriptor( PProcessPMethodCommunicationArea.class ) );
		bytecode.addAstore( localVariables.getComAreaRefIndex() );

		// make sure this invocation is performed through the container
		bytecode.addAload( localVariables.getComAreaRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( PProcessPMethodCommunicationArea.class ), GET_IS_CONTAINER_INVOCATION_METHOD, getJVMMethodDescriptor( boolean.class ) );
		int branchBlockBegin = bytecode.get().length;
		bytecode.add( Opcode.IFNE );
		bytecode.add( 0, 0 );
		bytecode.addNew( getJVMClassName( Error.class ) );
		bytecode.add( Opcode.DUP );
		bytecode.addLdc( ConstPoolHelper.getStringInfo( constPool, "Illegal invocation of an instrumented method by an uninstrumented method." ) );
		bytecode.addInvokespecial( getJVMClassName( Error.class ), INIT_METHOD, getJVMMethodDescriptor( void.class, String.class ) );
		bytecode.addCheckcast( getJVMClassName( Throwable.class ) );
		bytecode.add( Opcode.ATHROW );
		int branchBlockEnd = bytecode.get().length;
		int branchBlockSize = branchBlockEnd - branchBlockBegin;
		bytecode.write( branchBlockBegin + 2, branchBlockSize );

		// now mark this container invocation as completed
		bytecode.addAload( localVariables.getComAreaRefIndex() );
		bytecode.add( Opcode.ICONST_0 );
		bytecode.addInvokevirtual( getJVMClassName( PProcessPMethodCommunicationArea.class ), SET_IS_CONTAINER_INVOCATION_METHOD, getJVMMethodDescriptor( void.class, boolean.class ) );

		// store execution state in local variable
		bytecode.addInvokestatic( getJVMClassName( PersistentProcessImpl.class ), GET_CURRENT_PFRAME_OF_CURRENT_PROCESS_METHOD, getJVMMethodDescriptor( PFrame.class ) );
		bytecode.addInvokevirtual( getJVMClassName( PFrame.class ), GET_EXECUTION_STATE_METHOD, getJVMMethodDescriptor( ExecutionState.class ) );
		bytecode.addAstore( localVariables.getExecutionStateRefIndex() );

		// store method invocation in local variable
		bytecode.addAload( localVariables.getComAreaRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( PProcessPMethodCommunicationArea.class ), GET_INVOKED_METHOD_NAME_METHOD, getJVMMethodDescriptor( FQJVMMethodName.class ) );
		bytecode.addAstore( localVariables.getInvokedMethodRefIndex() );

		// store business stack trace element in local variable
		bytecode.addInvokestatic( getJVMClassName( PersistentProcessImpl.class ), GET_CURRENT_PFRAME_OF_CURRENT_PROCESS_METHOD, getJVMMethodDescriptor( PFrame.class ) );
		bytecode.addInvokevirtual( getJVMClassName( PFrame.class ), GET_STACK_TRACE_ELEMENT_METHOD, getJVMMethodDescriptor( StackTraceElement.class ) );
		bytecode.addAstore( localVariables.geStackTraceElemIndex() );

		// load the programCounter into the correct register
		bytecode.addAload( localVariables.getExecutionStateRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), GET_PROGRAM_COUNTER_METHOD, getJVMMethodDescriptor( int.class ) );
		bytecode.addIstore( localVariables.getProgramCounterIndex() );

		// generate branch instructions - values will be set
		// later once the actual target addresses are known
		for ( int i = 0; i < branchEntries.size(); ++i )
		{
			BranchEntry branchEntry = branchEntries.get( i );

			bytecode.addIload( localVariables.getProgramCounterIndex() );

			branchEntry.setGetPcInstructionIndex( bytecode.getSize() );
			bytecode.add( Opcode.LDC_W );
			bytecode.add( 0, 0 );

			branchEntry.setBranchInstructionIndex( bytecode.getSize() );
			bytecode.add( Opcode.IF_ICMPEQ );
			bytecode.add( 0, 0 );
		}

		InstrumentationHelper.emitComment( bytecode, constPool, "getPreambleBytecode() - end" );

		return bytecode;
	}

	private void replaceJumps( GenerateCodeResult generateCodeResult )
	{
		List< BranchEntry > branchEntries = generateCodeResult.getBranchEntries();
		ConstPool constPool = codeAttribute.getConstPool();
		byte[] byteCode = codeAttribute.getCode();

		for ( int i = 0 ; i < branchEntries.size() ; ++i )
		{
			BranchEntry branchEntry = branchEntries.get( i );
			
			int branchTargetIndex = branchEntry.getBranchTargetIndex();
			int branchInstructionIndex = branchEntry.getBranchInstructionIndex();
			int getPcInstructionIndex = branchEntry.getGetPcInstructionIndex();
			int setPcInstructionIndex = branchEntry.getSetPcInstructionIndex();

			int branchOffset = branchTargetIndex - branchInstructionIndex;

			byteCode[ branchInstructionIndex + 1 ] = (byte)( branchOffset >> 8 );
			byteCode[ branchInstructionIndex + 2 ] = (byte)branchOffset;

			// Historical note: originally, the PC instruction index stored in
			// ExecutionState.programCounter was actually the program counter,
			// however, this caused problems with processes with lifecycles
			// spanning multiple containers because the instrumented classes
			// exhibited small variations (probably due to non-deterministic
			// ordering of non-ordered collections, such as sets) resulting
			// in (occasionally) differing jump targets.
			// By using the list index i rather than the bytecode index of the
			// target location we effectively introduce a level of indirection
			// that guarantees correct jumping even if the target location
			// changes.
			// Note that we add 1 to the index location (i + 1) since 0 is used
			// on the first invocation to signal that no jump should occur.
			int constIndex = ConstPoolHelper.getIntegerInfo( constPool, i + 1 );

			byteCode[ getPcInstructionIndex + 1 ] = (byte)( constIndex >> 8 );
			byteCode[ getPcInstructionIndex + 2 ] = (byte)constIndex;

			byteCode[ setPcInstructionIndex + 1 ] = (byte)( constIndex >> 8 );
			byteCode[ setPcInstructionIndex + 2 ] = (byte)constIndex;
		}
	}
	
	private void adjustExceptionTable( GenerateCodeResult generateCodeResult )
	{
		ExceptionTable exceptionTable = codeAttribute.getExceptionTable();
		List< BranchEntry > instrumentedRegions = generateCodeResult.getBranchEntries();
		List< ExceptionTableEntry > exceptionRegions = ExceptionTableEntry.convert( exceptionTable );
		
		for ( BranchEntry instrumentedRegion : instrumentedRegions )
		{
			List< ExceptionTableEntry > intersectingRegions = getIntersectingRegions( instrumentedRegion, exceptionRegions );
			List< ExceptionTableEntry > adjustedRegions = getAdjustedRegions( instrumentedRegion, intersectingRegions );
			exceptionRegions.removeAll( intersectingRegions );
			exceptionRegions.addAll( adjustedRegions );
		}
		
		// Note that the sort isn't necessary; it just makes
		// the bytecode a bit more readable.
		ExceptionTableEntry.sort( exceptionRegions );
		ExceptionTableEntry.replaceExceptionTable( exceptionTable, exceptionRegions );
	}
	
	private List< ExceptionTableEntry > getIntersectingRegions( BranchEntry instrumentedRegion, List< ExceptionTableEntry > exceptionRegions )
	{
		List< ExceptionTableEntry > intersectingRegions = new ArrayList< ExceptionTableEntry >();

		for ( ExceptionTableEntry exceptionRegion : exceptionRegions )
		{
			if ( exceptionRegion.getStartPc() <= instrumentedRegion.getBeginInsertionIndex() &&
					exceptionRegion.getEndPc() >= instrumentedRegion.getEndInsertionIndex() )
			{
				intersectingRegions.add( exceptionRegion );
			}
		}
		
		return intersectingRegions;
	}
	
	private List< ExceptionTableEntry > getAdjustedRegions( BranchEntry instrumentedRegion, List< ExceptionTableEntry > intersectingRegions )
	{
		List< ExceptionTableEntry > adjustedRegions = new ArrayList< ExceptionTableEntry >();

		for ( ExceptionTableEntry intersectingRegion : intersectingRegions )
		{
			// Region prior to instrumentation.
			if ( intersectingRegion.getStartPc() < instrumentedRegion.getBeginInsertionIndex() )
				adjustedRegions.add( new ExceptionTableEntry(
					intersectingRegion.getStartPc(),
					instrumentedRegion.getBeginInsertionIndex(),
					intersectingRegion.getHandlerPc(),
					intersectingRegion.getCatchType() ) );
			
			// Region subsequent to instrumentation.
			if ( intersectingRegion.getEndPc() > instrumentedRegion.getEndInsertionIndex() )
				adjustedRegions.add( new ExceptionTableEntry(
					instrumentedRegion.getEndInsertionIndex(),
					intersectingRegion.getEndPc(),
					intersectingRegion.getHandlerPc(),
					intersectingRegion.getCatchType() ) );
				
			// Instrumented exception handling region (where we perform an athrow)
			// of any exception that was thrown by a method invoked on behalf of the
			// instrumented method.
			adjustedRegions.add( new ExceptionTableEntry(
				instrumentedRegion.getThrownExceptionIndex(),
				instrumentedRegion.getThrownExceptionIndex() + 1,
				intersectingRegion.getHandlerPc(),
				intersectingRegion.getCatchType() ) );
		}
		
		return adjustedRegions;
	}
}
