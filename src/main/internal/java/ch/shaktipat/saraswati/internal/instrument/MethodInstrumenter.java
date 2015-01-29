package ch.shaktipat.saraswati.internal.instrument;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ByteArray;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.Type;
import ch.shaktipat.exwrapper.javassist.bytecode.DescriptorWrapper;
import ch.shaktipat.exwrapper.javassist.bytecode.analysis.AnalyzerWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.common.SaraswatiHashSet;
import ch.shaktipat.saraswati.internal.instrument.method.BytecodeAdjuster;
import ch.shaktipat.saraswati.internal.instrument.method.BytecodeGenerator;
import ch.shaktipat.saraswati.internal.instrument.method.FrameHelper;
import ch.shaktipat.saraswati.internal.instrument.method.GenerateCodeResult;
import ch.shaktipat.saraswati.internal.instrument.method.InstrumentationHelper;
import ch.shaktipat.saraswati.internal.instrument.method.LocalVariables;
import ch.shaktipat.saraswati.internal.instrument.method.MethodInvocation;

public class MethodInstrumenter
{
	private CtMethod ctMethod = null;
	private CtClass ctClass = null;
	private ClassPool classPool = null;
	private ConstPool constPool = null;
	private Frame[] originalFrames;
	private Frame[] adjustedFrames;
	private BytecodeGenerator byteCodeGenerator;
	private BytecodeAdjuster bytecodeAdjuster;
	
	public MethodInstrumenter( CtMethod ctMethod )
	{
		super();
		this.ctMethod = ctMethod;
		ctClass = ctMethod.getDeclaringClass();
		classPool = ctClass.getClassPool();
		constPool = ctClass.getClassFile().getConstPool();
		byteCodeGenerator = new BytecodeGenerator( ctMethod );
		bytecodeAdjuster = new BytecodeAdjuster( ctMethod );
		
		Analyzer analyzer = new Analyzer();
		originalFrames = AnalyzerWrapper.analyze( analyzer, ctClass, ctMethod.getMethodInfo() );
	}

	public void instrumentMethod()
	{
		adjustedFrames = duplicateFrames( originalFrames );
		syncFramesWithLocalVariableTable();
		instrumentObjectCreationInstances();
		instrumentMethodInvocations();
	}
	
	public Frame[] duplicateFrames( Frame[] frames )
	{
		// Copy frames to adjustedFrames.
		Frame[] newFrames = new Frame[ originalFrames.length ];
		
		for ( int i = 0 ; i < frames.length ; ++i )
			if ( frames[ i ] != null )
				newFrames[ i ] = frames[ i ].copy();
		
		return newFrames;
	}
	
	private void instrumentObjectCreationInstances()
	{
		Set< NewDupInvokespecialTuple > ndiTuples = identifyObjectCreationInstances();
		replaceObjectCreationInstances( ndiTuples );
		determineFramesAdjustedForObjectCreation( ndiTuples );
	}
	
	// This method fixes a bug in javassist 3.18.0-GA that causes the registers of
	// some frames to be set outside the bounds of what is defined within the local
	// variable table.
	// Unfortunately, by doing so, it also erases any non-local variable register
	// usages, which occur, for example, when compiling try/finally expressions.
	// Thus, in the following case:
	// 		try	{ throw new RuntimeException(); }
	//      finally { System.out.println( "testing" ); }
	// The bytecode will actually store RuntimeException in a register while 
	// System.out.println() is executing before retreiving and throwing it afterwards.
	// Basically, the analyzer in javassist doesn't do what I need (and possibly doesn't
	// event really work correctly, unless I'm misunderstanding something) and I don't
	// know how to fix it without writing my own analyzer.
	private void syncFramesWithLocalVariableTable()
	{
		for ( RegisterHistory registerHistory : RegisterHistory.getRegisterHistories( ctMethod ) )
		{
			int register = registerHistory.getRegister();
			
			for ( int j = 0 ; j < adjustedFrames.length ; ++j )
			{
				// Skip null frames.
				if ( adjustedFrames[ j ] == null )
					continue;

				// If a type is set for this frame and register but there is
				// no register history at that location --> set the type to null.
				Type type = adjustedFrames[ j ].getLocal( register );
				
				if ( type != null && !registerHistory.isInRange( j ) )
					adjustedFrames[ j ].setLocal( register, null );
			}
		}
	}
	
	private void determineFramesAdjustedForObjectCreation( Set< NewDupInvokespecialTuple > ndiTuples )
	{
		final int LENGTH_OF_NEW_INSTRUCTION = 3;
		final int LENGTH_OF_DUP_INSTRUCTION = 1;
		
		// Iterate through object creation instances.
		for ( NewDupInvokespecialTuple ndiTuple : ndiTuples )
		{
			// Make a note of key bytecode and stack locations.
			int startFrame = ndiTuple.getLocationOfNewDup() + LENGTH_OF_NEW_INSTRUCTION;
			int endFrame = ndiTuple.getLocationOfInvokespecial();
			int stackIndexOfNewReference = adjustedFrames[ startFrame ].getTopIndex();
			int stackIndexOfDupReference = stackIndexOfNewReference + 1;
			
			// Adjust the frame after the NEW instruction.
			adjustedFrames[ startFrame ].pop();
			
			for ( int i = startFrame + LENGTH_OF_DUP_INSTRUCTION; i <= endFrame ; ++i )
			{
				Frame adjustedFrame = adjustedFrames[ i ];
				
				// If there is no frame at this location --> continue;
				if ( adjustedFrame == null )
					continue;
				
				// Otherwise, remove the NEW/DUP references from this frame's stack.
				Frame newAdjustedFrame = adjustedFrame.copy();
				newAdjustedFrame.clearStack();
				
				for ( int k = 0 ; k < adjustedFrame.getTopIndex() + 1 ; ++k )
				{
					if ( k == stackIndexOfNewReference || k == stackIndexOfDupReference )
						continue;
					
					newAdjustedFrame.push( adjustedFrame.getStack( k ) );
				}
				
				adjustedFrames[ i ] = newAdjustedFrame;
			}
		}
	}
	
	private void replaceObjectCreationInstances( Set< NewDupInvokespecialTuple > ndiTuples )
	{
		CodeIterator codeIterator = ctMethod.getMethodInfo().getCodeAttribute().iterator();
		
		for ( NewDupInvokespecialTuple ndiTuple : ndiTuples )
		{
			int newDupLocation = ndiTuple.getLocationOfNewDup();
			int invokeSpecialLocation = ndiTuple.getLocationOfInvokespecial();
			
			// Overwrite the NEW / DUP combo.
			codeIterator.writeByte( Opcode.NOP, newDupLocation );
			codeIterator.writeByte( Opcode.NOP, newDupLocation + 1 );
			codeIterator.writeByte( Opcode.NOP, newDupLocation + 2 );
			codeIterator.writeByte( Opcode.NOP, newDupLocation + 3 );
			
			// Overwrite the <init> method name with SARASWATI_NEW_MARKER_METHOD.
			int initMethodRef = codeIterator.u16bitAt( invokeSpecialLocation + 1  );
			String className = constPool.getMethodrefClassName( initMethodRef );
			String methodDescriptor = constPool.getMethodrefType( initMethodRef );
			
			int saraswatiNewRefIndex = ConstPoolHelper.getMethodRefInfo( constPool, className, FieldAndMethodConstants.SARASWATI_NEW_MARKER_METHOD, methodDescriptor );
			codeIterator.write16bit( saraswatiNewRefIndex, invokeSpecialLocation + 1 );
		}
	}

	private Set< NewDupInvokespecialTuple > identifyObjectCreationInstances()
	{
		byte[] bytecode = ctMethod.getMethodInfo().getCodeAttribute().getCode();
		Stack< NewDupInvokespecialTuple > ndiTupleStack = new Stack< NewDupInvokespecialTuple >();

		for ( int i = 0; i < originalFrames.length; ++i )
		{
			Frame frame = originalFrames[ i ];

			if ( frame == null )
				continue;

			int instruction = bytecode[ i ] & 0xff;

			if ( instruction == Opcode.NEW )
			{
				ndiTupleStack.push( new NewDupInvokespecialTuple( i ) );
			}
			else if ( instruction == Opcode.INVOKESPECIAL )
			{
				Frame invokespecailFrame = getAdjustedInvokespecialFrame( bytecode, originalFrames, i );
				
				if ( invokespecailFrame == null )
					continue;

				for ( int k = ndiTupleStack.size() - 1; k > -1; --k )
				{
					NewDupInvokespecialTuple ndiTuple = ndiTupleStack.get( k );

					// Only consider ndiTuples that have not yet been matched.
					if ( ndiTuple.getLocationOfInvokespecial() != -1 )
						continue;
					
					Frame newDupFrame = getNewDupFrame( originalFrames, ndiTuple );
					
					if ( stacksAreEqual( newDupFrame, invokespecailFrame ) )
					{
						ndiTuple.setLocationOfInvokespecial( i );
						break;
					}
				}
			}
		}

		// Sanity check.
		for ( NewDupInvokespecialTuple ndiTuple : ndiTupleStack )
			if ( ndiTuple.getLocationOfInvokespecial() == NewDupInvokespecialTuple.UNDEFINED_VALUE )
				throw new IllegalStateException( "Internal error: found a NEW without an associated INVOKESPECIAL." );

		Set< NewDupInvokespecialTuple > ndiTupleSet = new SaraswatiHashSet< NewDupInvokespecialTuple >();
		ndiTupleSet.addAll( ndiTupleStack );

		return ndiTupleSet;
	}
	
	private boolean stacksAreEqual( Frame frame1, Frame frame2 )
	{
		Stack< Type > frame1Stack = new Stack< Type >();
		Stack< Type > frame2Stack = new Stack< Type >();
		
		for ( int i = 0 ; i < frame1.getTopIndex() + 1 ; ++i )
			frame1Stack.push( frame1.getStack( i ) );

		for ( int i = 0 ; i < frame2.getTopIndex() + 1 ; ++i )
			frame2Stack.push( frame2.getStack( i ) );
		
		return frame1Stack.equals( frame2Stack );
	}

	private Frame getAdjustedInvokespecialFrame( byte[] bytecode, Frame[] frames, int i )
	{
		Frame adjustedInvokespecialFrame = null;
		
		int index = ByteArray.readU16bit( bytecode, i + 1 );
		String methodrefName = constPool.getMethodrefName( index );
		
		if ( !methodrefName.equals( "<init>" ) )
			return adjustedInvokespecialFrame;
		
		String methodrefType = constPool.getMethodrefType( index );
		CtClass[] paramTypes = DescriptorWrapper.getParameterTypes( methodrefType, classPool );

		adjustedInvokespecialFrame = frames[ i ].copy();

		for ( int k = 0 ; k < paramTypes.length ; ++k )
			FrameHelper.pop( adjustedInvokespecialFrame );

		return adjustedInvokespecialFrame;
	}

	private Frame getNewDupFrame( Frame[] frames, NewDupInvokespecialTuple ndiTuple )
	{
		// +4 to get to the frame immediately after the NEW / DUP
		return frames[ ndiTuple.getLocationOfNewDup() + 4 ];
	}
	
	private void instrumentMethodInvocations()
	{
		List< MethodInvocation > methodInvocations = InstrumentationHelper.getMethodInvocations( ctMethod.getMethodInfo(), classPool, constPool, originalFrames );

		// Create local variables
		// Note that this happens *after* the frame analyzer is run to
		// ensure that the Frame.localsLength() is not effected.
		LocalVariables localVariables = new LocalVariables( ctMethod );
		
		// Generate bytecode
		GenerateCodeResult generateCodeResult = byteCodeGenerator.getGeneratedBytecode( methodInvocations, adjustedFrames, localVariables );

		// Make adjustments
		bytecodeAdjuster.adjustByteCode( generateCodeResult, methodInvocations, localVariables );
	}
}
