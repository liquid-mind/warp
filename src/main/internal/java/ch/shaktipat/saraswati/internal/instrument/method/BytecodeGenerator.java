package ch.shaktipat.saraswati.internal.instrument.method;

import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.BOOLEAN_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.BYTE_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.CHAR_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.DOUBLE_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.FLOAT_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_EXCEPTION_OCCURED_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_LOCAL_VARIABLES_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.GET_OPERAND_STACK_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.INT_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.LONG_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_JVM_CLASS_NAME_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_JVM_METHOD_DESCRIPTOR_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_JVM_METHOD_NAME_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_LINE_NUMBER_OF_CURRENT_PROCESS_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_LOCAL_VARIABLES_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_METHOD_INVOCATION_TYPE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_OPERAND_STACK_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SET_PROGRAM_COUNTER_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.SHORT_VALUE_METHOD;
import static ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants.VALUE_OF_METHOD;
import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.getJVMClassName;
import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.getJVMFieldDescriptor;
import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.getJVMMethodDescriptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.Type;
import ch.shaktipat.exwrapper.javassist.bytecode.DescriptorWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.ExecutionState;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessPMethodCommunicationArea;

/*
 * Note on comparisons of javassist.bytecode.analysis.Type: Type.equals() returns true
 * in some unexpected cases, e.g., Type.TOP.equals( Type.UNINIT ) returns true. For this
 * reason, these special cases are handled by using object ref comparisons (==) which works
 * since these are static constants and there is only ever one instance per classloader.
 * 
 * A few notes on "wide" operations: there are a few opcodes that come in both "normal" and
 * "wide" flavors, specifically goto / goto_w, jsr / jsr_w and ldc / ldc_w / ldc2_w.
 * Theoretically, it would be possible for the extra instrumented code to cause a previously
 * valid "normal" opcode to become invalid, due to an overflow of branch-offset size, however:
 * -as far as I can tell, goto_w and jsr_w are never actually generated, since goto / jsr
 * take 2-byte branch-offsets and the VM spec limits the size of a method's byte code to 2^16
 * anyway (BTW: this also explains if_x opcodes taking 2-byte branch-offsets)
 * -ldc / ldc_w in instrumented code is handled automatically by javassist's addLdc() method.
 * -ldc / ldw_w in the original byte code can stay are they are, since instrumentation does
 *  not change existing constants, but rather merely adds new ones.
 *  
 *  BTW: the instrumentation for a given method requires about 200 bytes (depending on the
 *  number of local variables and the size of the stack) and - due to the max size of byte code
 *  being limited to 2^16 (65536) - this means that the maximum number of invocations 
 *  an instrumented method may perform is roughly 300. I assume that in most cases this won't be
 *  a problem, but... ?
 */
public class BytecodeGenerator
{
	private static final Logger logger = Logger.getLogger( BytecodeGenerator.class.getName() );

	private static final Set< Type > INTEGRAL_TYPES;
	private static final Set< Type > PRIMITIVE_TYPES;
	private static final Set< Type > TWO_WORD_TYPES;

	static
	{
		INTEGRAL_TYPES = new HashSet< Type >();
		INTEGRAL_TYPES.add( Type.BOOLEAN );
		INTEGRAL_TYPES.add( Type.BYTE );
		INTEGRAL_TYPES.add( Type.CHAR );
		INTEGRAL_TYPES.add( Type.SHORT );
		INTEGRAL_TYPES.add( Type.INTEGER );

		PRIMITIVE_TYPES = new HashSet< Type >();
		PRIMITIVE_TYPES.addAll( INTEGRAL_TYPES );
		PRIMITIVE_TYPES.add( Type.LONG );
		PRIMITIVE_TYPES.add( Type.FLOAT );
		PRIMITIVE_TYPES.add( Type.DOUBLE );

		TWO_WORD_TYPES = new HashSet< Type >();
		TWO_WORD_TYPES.add( Type.DOUBLE );
		TWO_WORD_TYPES.add( Type.LONG );
	}

	private ClassPool classPool;
	private ConstPool constPool;
	private LocalVariables localVariables;
	private MethodInfo methodInfo;
	private Bytecode bytecode;

	public BytecodeGenerator( CtMethod ctMethod )
	{
		this.classPool = ctMethod.getDeclaringClass().getClassPool();
		this.methodInfo = ctMethod.getMethodInfo();
		this.constPool = methodInfo.getConstPool();
	}

	public GenerateCodeResult getGeneratedBytecode( List< MethodInvocation > methodInvocations, Frame[] adjustedFrames, LocalVariables localVariables )
	{
		this.localVariables = localVariables;

		List< BytecodeInsertion > bytecodeInsertions = new ArrayList< BytecodeInsertion >();
		List< BranchEntry > branchEntries = new ArrayList< BranchEntry >();

		for ( int i = 0; i < methodInvocations.size(); ++i )
		{
			bytecode = new Bytecode( constPool );

			MethodInvocation mInvocation = methodInvocations.get( i );
			FQJVMMethodName methodName =  mInvocation.getFQMethodName();
			logger.fine( "    Instrumenting method invocation: " + methodName.getJVMClassName() + "." + methodName.getJVMMethodName() + methodName.getJVMMethodDescriptor() );
			InstrumentationHelper.emitComment( bytecode, constPool,
				"Instrumenting method invocation: " + methodName.getJVMClassName() + "." + methodName.getJVMMethodName() + methodName.getJVMMethodDescriptor() + " - begin" );

			int insertionIndex = mInvocation.getIndex();

			BranchEntry branchEntry = new BranchEntry();
			branchEntries.add( branchEntry );
			branchEntry.setBeginInsertionIndex( 0 );
			Frame frameAtInvocation = adjustedFrames[ insertionIndex ];
			Frame frameAfterInvocation = getNextFrame( adjustedFrames, insertionIndex );

			// Method "invocation" instrumentation
			setInvokedMethod( mInvocation );
			moveOperandStackToExecutionState( frameAtInvocation );
			copyLocalVariablesToExecutionState( frameAtInvocation );
			setProgramCounter( branchEntry );
			setLineNumber( mInvocation );
			emitReturn();

			branchEntry.setBranchTargetIndex( bytecode.getSize() );

			// Method "return" instrumentation
			getLocalVariablesFromExecutionState( frameAfterInvocation );
			prepareExecutionStateToOperandStack();
			emitExceptionHandler( mInvocation, frameAfterInvocation, branchEntry );
			moveExecutionStateToOperandStack( mInvocation, frameAfterInvocation );

			InstrumentationHelper.emitComment( bytecode, constPool,
				"Instrumenting method invocation: " + methodName.getJVMClassName() + "." + methodName.getJVMMethodName() + methodName.getJVMMethodDescriptor() + " - end" );

			branchEntry.setEndInsertionIndex( bytecode.length() );

			BytecodeInsertion bcInsertion = new BytecodeInsertion( insertionIndex, i, bytecode );
			bytecodeInsertions.add( bcInsertion );
		}

		return new GenerateCodeResult( bytecodeInsertions, branchEntries );
	}
	
	private Frame getNextFrame( Frame[] frames, int index )
	{
		Frame nextFrame = null;
		
		for ( int i = index + 1 ; i < frames.length ; ++i )
			if ( (nextFrame=frames[ i ]) != null )
				break;
		
		assert( nextFrame != null );
		
		return nextFrame;
	}

	private void setInvokedMethod( MethodInvocation mInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "setInvokedMethod()" );
		FQJVMMethodName methodName = mInvocation.getFQMethodName();

		// set the class name
		bytecode.addAload( localVariables.getInvokedMethodRefIndex() );
		bytecode.addLdc( ConstPoolHelper.getStringInfo( constPool, methodName.getJVMClassName() ) );
		bytecode.addInvokevirtual( getJVMClassName( FQJVMMethodName.class ), SET_JVM_CLASS_NAME_METHOD, getJVMMethodDescriptor( void.class, String.class ) );

		// set the method name
		bytecode.addAload( localVariables.getInvokedMethodRefIndex() );
		bytecode.addLdc( ConstPoolHelper.getStringInfo( constPool, methodName.getJVMMethodName() ) );
		bytecode.addInvokevirtual( getJVMClassName( FQJVMMethodName.class ), SET_JVM_METHOD_NAME_METHOD, getJVMMethodDescriptor( void.class, String.class ) );

		// set the method signature
		bytecode.addAload( localVariables.getInvokedMethodRefIndex() );
		bytecode.addLdc( ConstPoolHelper.getStringInfo( constPool, methodName.getJVMMethodDescriptor() ) );
		bytecode.addInvokevirtual( getJVMClassName( FQJVMMethodName.class ), SET_JVM_METHOD_DESCRIPTOR_METHOD, getJVMMethodDescriptor( void.class, String.class ) );

		// set the method invocation type
		bytecode.addAload( localVariables.getComAreaRefIndex() );
		bytecode.addGetstatic( getJVMClassName( MethodInvocationType.class ), mInvocation.getMethodInvocationType().toString(), getJVMFieldDescriptor( MethodInvocationType.class ) );
		bytecode.addInvokevirtual( getJVMClassName( PProcessPMethodCommunicationArea.class ), SET_METHOD_INVOCATION_TYPE_METHOD, getJVMMethodDescriptor( void.class, MethodInvocationType.class ) );
	}

	private void moveOperandStackToExecutionState( Frame frameAtInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "moveOperandStackToExecutionState()" );

		int arraySize = calculateOpStackArraySize( frameAtInvocation );
		int arrayCounter = arraySize;
		int stackSize = frameAtInvocation.getTopIndex() + 1;

		// create the invocation object array and store reference in designated
		// local variable
		bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, arraySize ) );
		bytecode.addAnewarray( getJVMClassName( Object.class ) );
		bytecode.addAstore( localVariables.getArrayRefIndex() );

		// initialize the array dim 1 counter
		bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, arraySize - 1 ) );
		bytecode.addIstore( localVariables.getArrayCounterIndex() );

		// Note: 0 is the bottom of the stack,
		// stackSize - 1 is the top of the stack.
		for ( int i = stackSize - 1; i >= 0; --i )
		{
			Type stackType = frameAtInvocation.getStack( i );

			// Note: TOP is the second word position of a double-word type
			// (i.e., long, double)
			if ( !stackTypeIsValid( stackType ) )
				continue;

			popTopOfOperandStackToArray( stackType );

			// decrement the array dim 1 counter only if
			// there are more array elements
			if ( --arrayCounter > 0 )
			{
				bytecode.add( Opcode.IINC );
				bytecode.add( localVariables.getArrayCounterIndex() );
				bytecode.add( -1 );
			}
		}

		// set the operand stack in the execution state
		bytecode.addAload( localVariables.getExecutionStateRefIndex() );
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), SET_OPERAND_STACK_METHOD, getJVMMethodDescriptor( void.class, Object[].class ) );
	}

	private void prepareExecutionStateToOperandStack()
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "prepareExecutionStateToOperandStack()" );

		// load the operand stack into the correct register
		bytecode.addAload( localVariables.getExecutionStateRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), GET_OPERAND_STACK_METHOD, getJVMMethodDescriptor( Object[].class ) );
		bytecode.addAstore( localVariables.getArrayRefIndex() );

		// initialize the array dim 2 counter
		bytecode.addIconst( 0 );
		bytecode.addIstore( localVariables.getArrayCounterIndex() );
	}

	private void moveExecutionStateToOperandStack( MethodInvocation mInvocation, Frame frameAfterInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "moveExecutionStateToOperandStack()" );

		int arrayCounter = calculateOpStackArraySize( frameAfterInvocation );
		int stackSize = frameAfterInvocation.getTopIndex() + 1;

		for ( int i = 0; i < stackSize; ++i )
		{
			Type stackType = frameAfterInvocation.getStack( i );

			// Note: TOP is the second word position of a double-word type
			// (i.e., long, double)
			if ( !stackTypeIsValid( stackType ) )
				continue;

			pushArrayItemToOperandStack( stackType );

			// increment the array dim 1 counter only if
			// there are more array elements
			if ( --arrayCounter > 0 )
			{
				bytecode.add( Opcode.IINC );
				bytecode.add( localVariables.getArrayCounterIndex() );
				bytecode.add( 1 );
			}
		}
	}

	private void pushArrayItemToOperandStack( Type type )
	{
		assert ( type != null );
		assert ( PRIMITIVE_TYPES.contains( type ) || type.isReference() );

		logger.fine( "      pushArrayItemToOperandStack(), type = " + type );
		InstrumentationHelper.emitComment( bytecode, constPool, "pushArrayItemToOperandStack(), type = " + type );

		// load the item at the current array index
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addIload( localVariables.getArrayCounterIndex() );
		bytecode.add( Opcode.AALOAD );

		if ( PRIMITIVE_TYPES.contains( type ) )
		{
			if ( INTEGRAL_TYPES.contains( type ) )
			{
				// convert wrapper type to corresponding primitive type
				if ( type.equals( Type.BOOLEAN ) )
				{
					bytecode.addCheckcast( Boolean.class.getName() );
				}
				else if ( type.equals( Type.BYTE ) )
				{
					bytecode.addCheckcast( Byte.class.getName() );
				}
				else if ( type.equals( Type.CHAR ) )
				{
					bytecode.addCheckcast( Character.class.getName() );
				}
				else if ( type.equals( Type.SHORT ) )
				{
					bytecode.addCheckcast( Short.class.getName() );
				}
				else if ( type.equals( Type.INTEGER ) )
				{
					bytecode.addCheckcast( Integer.class.getName() );
				}
			}
			else if ( type.equals( Type.LONG ) )
			{
				bytecode.addCheckcast( Long.class.getName() );
			}
			else if ( type.equals( Type.FLOAT ) )
			{
				bytecode.addCheckcast( Float.class.getName() );
			}
			else if ( type.equals( Type.DOUBLE ) )
			{
				bytecode.addCheckcast( Double.class.getName() );
			}

			convertWrapperToPrimitive( type );
		}
		else if ( type.isReference() )
		{
			bytecode.addCheckcast( type.getCtClass() );
		}
		else
		{
			assert false;
		}
	}

	private void popTopOfOperandStackToArray( Type type )
	{
		assert type != null;
		assert PRIMITIVE_TYPES.contains( type ) || type.isReference();

		logger.fine( "      popTopOfOperandStackToArray(), type = " + type );
		InstrumentationHelper.emitComment( bytecode, constPool, "popTopOfOperandStackToArray(), type = " + type );

		if ( PRIMITIVE_TYPES.contains( type ) )
		{
			if ( INTEGRAL_TYPES.contains( type ) )
			{
				// move the next parameter to the designated local variable
				bytecode.addIstore( localVariables.getExecutionStateValueIndex() );

				// put the array reference, the array index and
				// the local variable onto the stack
				bytecode.addAload( localVariables.getArrayRefIndex() );
				bytecode.addIload( localVariables.getArrayCounterIndex() );
				bytecode.addIload( localVariables.getExecutionStateValueIndex() );
			}
			else if ( type.equals( Type.LONG ) )
			{
				bytecode.addLstore( localVariables.getExecutionStateValueIndex() );
				bytecode.addAload( localVariables.getArrayRefIndex() );
				bytecode.addIload( localVariables.getArrayCounterIndex() );
				bytecode.addLload( localVariables.getExecutionStateValueIndex() );
			}
			else if ( type.equals( Type.FLOAT ) )
			{
				bytecode.addFstore( localVariables.getExecutionStateValueIndex() );
				bytecode.addAload( localVariables.getArrayRefIndex() );
				bytecode.addIload( localVariables.getArrayCounterIndex() );
				bytecode.addFload( localVariables.getExecutionStateValueIndex() );
			}
			else if ( type.equals( Type.DOUBLE ) )
			{
				bytecode.addDstore( localVariables.getExecutionStateValueIndex() );
				bytecode.addAload( localVariables.getArrayRefIndex() );
				bytecode.addIload( localVariables.getArrayCounterIndex() );
				bytecode.addDload( localVariables.getExecutionStateValueIndex() );
			}

			convertPrimitiveToWrapper( type );
		}
		else if ( type.isReference() )
		{
			bytecode.addAstore( localVariables.getExecutionStateValueIndex() );
			bytecode.addAload( localVariables.getArrayRefIndex() );
			bytecode.addIload( localVariables.getArrayCounterIndex() );
			bytecode.addAload( localVariables.getExecutionStateValueIndex() );
		}
		else
		{
			throw new IllegalStateException();
		}

		// store the local variable into the array
		bytecode.add( Opcode.AASTORE );
	}

	private void copyLocalVariablesToExecutionState( Frame frameAtInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "copyLocalVariablesToExecutionState()" );

		// Is this a static method?
		int thisReferenceOffset = 1;
		int localsLowerBoundary = 1;
		if ( ( methodInfo.getAccessFlags() & AccessFlag.STATIC ) != 0 )
		{
			thisReferenceOffset = 0;
			localsLowerBoundary = 0;
		}
			
		int arraySize = calculateLocalVariablesArraySize( frameAtInvocation ) - thisReferenceOffset;
		int arrayCounter = arraySize;
		int localsSize = frameAtInvocation.localsLength();

		// create the local variables array and store reference in designated
		// local variable
		bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, arraySize ) );
		bytecode.addAnewarray( Object.class.getName() );
		bytecode.addAstore( localVariables.getArrayRefIndex() );

		// initialize the array dim 1 counter
		bytecode.addIconst( 0 );
		bytecode.addIstore( localVariables.getArrayCounterIndex() );

		// Note: if this is a non-static method then we start
		// with 1 since we are skipping the "this" reference
		// ("this" will be set by the VM automatically on each invocation...)
		for ( int i = localsLowerBoundary; i < localsSize; ++i )
		{
			Type localType = frameAtInvocation.getLocal( i );

			if ( !registerTypeIsValid( localType ) )
			{
				continue;
			}

			copyLocalVariableToArray( localType, i );

			// increment the array dim 1 counter only if
			// there are more array elements
			if ( --arrayCounter > 0 )
			{
				bytecode.add( Opcode.IINC );
				bytecode.add( localVariables.getArrayCounterIndex() );
				bytecode.add( 1 );
			}
		}

		// set the array to the execution state
		bytecode.addAload( localVariables.getExecutionStateRefIndex() );
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), SET_LOCAL_VARIABLES_METHOD, getJVMMethodDescriptor( void.class, Object[].class ) );
	}

	private void getLocalVariablesFromExecutionState( Frame frameAfterInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "getLocalVariablesFromExecutionState()" );

		// Is this a static method?
		int localsLowerBoundary = 1;
		if ( ( methodInfo.getAccessFlags() & AccessFlag.STATIC ) != 0 )
		{
			localsLowerBoundary = 0;
		}
					
		int arrayCounter = calculateLocalVariablesArraySize( frameAfterInvocation );

		// fetch the local variables array and store reference in designated
		// local variable
		bytecode.addAload( localVariables.getExecutionStateRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), GET_LOCAL_VARIABLES_METHOD, getJVMMethodDescriptor( Object[].class ) );
		bytecode.addAstore( localVariables.getArrayRefIndex() );

		// initialize the array dim 1 counter
		bytecode.addIconst( 0 );
		bytecode.addIstore( localVariables.getArrayCounterIndex() );

		int localsSize = frameAfterInvocation.localsLength();

		// Note: we start with 1 since we are skipping the "this" reference
		// ("this" will be set by the VM automatically on each invocation...)
		for ( int i = localsLowerBoundary; i < localsSize; ++i )
		{
			Type localType = frameAfterInvocation.getLocal( i );

			if ( !registerTypeIsValid( localType ) )
			{
				continue;
			}

			copyArrayItemToLocalVariable( localType, i );

			// increment the array dim 1 counter only if
			// there are more array elements
			if ( --arrayCounter > 0 )
			{
				bytecode.add( Opcode.IINC );
				bytecode.add( localVariables.getArrayCounterIndex() );
				bytecode.add( 1 );
			}
		}
	}

	/*
	 * Note: despite what the JVM documentation says (see link below) I was
	 * unable to produce a jsr / ret pairing by using a finally clause. For now
	 * I will ignore the jsr / ret eventuality, however at some point we would
	 * need to consider how to deal with it. The RETURN_ADDRESS type is a
	 * problem, since it is the only type we cannot manipulate with putfield and
	 * therefore cannot copy to the virtual stack. It might be possible to
	 * replace all occurances of jsr / ret with gotos... ? Need to
	 * investigate... For documentation on jsr / ret see:
	 * http://java.sun.com/docs
	 * /books/vmspec/2nd-edition/html/Instructions2.doc7.html
	 * http://java.sun.com
	 * /docs/books/vmspec/2nd-edition/html/Instructions2.doc12.html
	 */
	private void copyLocalVariableToArray( Type type, int registerIndex )
	{
		assert ( type != null );
		assert ( PRIMITIVE_TYPES.contains( type ) || type.isReference() );

		logger.fine( "      copyLocalVariableToArray(), type = " + type + ", registerIndex = " + registerIndex );
		InstrumentationHelper.emitComment( bytecode, constPool, "copyLocalVariableToArray(), type = " + type + ", registerIndex = " + registerIndex );

		// put the array reference and the array index onto the stack
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addIload( localVariables.getArrayCounterIndex() );

		if ( PRIMITIVE_TYPES.contains( type ) )
		{
			if ( INTEGRAL_TYPES.contains( type ) )
			{
				bytecode.addIload( registerIndex );
			}
			else if ( type.equals( Type.LONG ) )
			{
				bytecode.addLload( registerIndex );
			}
			else if ( type.equals( Type.FLOAT ) )
			{
				bytecode.addFload( registerIndex );
			}
			else if ( type.equals( Type.DOUBLE ) )
			{
				bytecode.addDload( registerIndex );
			}

			convertPrimitiveToWrapper( type );
		}
		else if ( type.isReference() )
		{
			if ( type != Type.UNINIT )
			{
				bytecode.addAload( registerIndex );
			}
			else
			{
				bytecode.addOpcode( Opcode.ACONST_NULL );
			}
		}
		else
		{
			throw new IllegalStateException();
		}

		// store the local variable into the array
		bytecode.add( Opcode.AASTORE );
	}

	private void copyArrayItemToLocalVariable( Type type, int registerIndex )
	{
		assert ( type != null );
		assert ( PRIMITIVE_TYPES.contains( type ) || type.isReference() );

		logger.fine( "      copyArrayItemToLocalVariable(), type = " + type + ", registerIndex = " + registerIndex );
		InstrumentationHelper.emitComment( bytecode, constPool, "copyArrayItemToLocalVariable(), type = " + type + ", registerIndex = " + registerIndex );

		// load the item at the current array index
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addIload( localVariables.getArrayCounterIndex() );
		bytecode.add( Opcode.AALOAD );

		if ( INTEGRAL_TYPES.contains( type ) )
		{
			bytecode.addCheckcast( Integer.class.getName() );
			convertWrapperToPrimitive( type );
			bytecode.addIstore( registerIndex );
		}
		else if ( type.equals( Type.LONG ) )
		{
			bytecode.addCheckcast( Long.class.getName() );
			convertWrapperToPrimitive( type );
			bytecode.addLstore( registerIndex );
		}
		else if ( type.equals( Type.FLOAT ) )
		{
			bytecode.addCheckcast( Float.class.getName() );
			convertWrapperToPrimitive( type );
			bytecode.addFstore( registerIndex );
		}
		else if ( type.equals( Type.DOUBLE ) )
		{
			bytecode.addCheckcast( Double.class.getName() );
			convertWrapperToPrimitive( type );
			bytecode.addDstore( registerIndex );
		}
		else if ( type.isReference() )
		{
			// Note: according to the javassist doc, UNINIT means
			// that aconst_null was used, i.e. we are dealing with
			// a reference (local) variable with a value of null.
			// We will handle this as a normal reference but without
			// the cast.
			if ( type != Type.UNINIT )
			{
				bytecode.addCheckcast( type.getCtClass() );
			}
			else
			{
				bytecode.addOpcode( Opcode.POP );
				bytecode.addOpcode( Opcode.ACONST_NULL );
			}

			bytecode.addAstore( registerIndex );
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	private void setProgramCounter( BranchEntry branchEntry )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "setProgramCounter()" );

		bytecode.addAload( localVariables.getExecutionStateRefIndex() );

		branchEntry.setSetPcInstructionIndex( bytecode.getSize() );
		bytecode.add( Opcode.LDC_W );
		bytecode.add( 0, 0 );
		bytecode.addInvokevirtual( getJVMClassName( ExecutionState.class ), SET_PROGRAM_COUNTER_METHOD, getJVMMethodDescriptor( void.class, int.class ) );
	}

	private void setLineNumber( MethodInvocation mInvocation )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "setLineNumber()" );
		
		bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, mInvocation.getLineNumber() ) );
		bytecode.addInvokestatic( getJVMClassName( PersistentProcessImpl.class ), SET_LINE_NUMBER_OF_CURRENT_PROCESS_METHOD, getJVMMethodDescriptor( void.class, int.class ) );
	}

	private void emitReturn()
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "emitReturn()" );
		
		CtClass returnCtClass = DescriptorWrapper.getReturnType( methodInfo.getDescriptor(), classPool );
		Type returnType = Type.get( returnCtClass );

		if ( returnType.equals( Type.VOID ) )
		{
			bytecode.add( Opcode.RETURN );
		}
		else if ( INTEGRAL_TYPES.contains( returnType ) )
		{
			bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, 0 ) );
			bytecode.add( Opcode.IRETURN );
		}
		else if ( returnType.equals( Type.DOUBLE ) )
		{
			bytecode.addLdc2w( 0.0D );
			bytecode.add( Opcode.DRETURN );
		}
		else if ( returnType.equals( Type.FLOAT ) )
		{
			bytecode.addLdc( ConstPoolHelper.getFloatInfo( constPool, 0.0F ) );
			bytecode.add( Opcode.FRETURN );
		}
		else if ( returnType.equals( Type.LONG ) )
		{
			bytecode.addLdc2w( 0L );
			bytecode.add( Opcode.LRETURN );
		}
		else
		{
			bytecode.add( Opcode.ACONST_NULL );
			bytecode.add( Opcode.ARETURN );
		}
	}

	private void emitExceptionHandler( MethodInvocation mInvocation, Frame frameAfterInvocation, BranchEntry branchEntry )
	{
		InstrumentationHelper.emitComment( bytecode, constPool, "emitExceptionHandler()" );

		// Load exceptionOccured field
		bytecode.addAload( localVariables.getComAreaRefIndex() );
		bytecode.addInvokevirtual( getJVMClassName( PProcessPMethodCommunicationArea.class ), GET_EXCEPTION_OCCURED_METHOD, getJVMMethodDescriptor( boolean.class ) );
		bytecode.addIconst( 0 );

		// Remember this location...
		int branchFromLocation = bytecode.length();

		// If exceptionOccured is not true --> jump to next section
		bytecode.add( Opcode.IF_ICMPEQ );
		// Note: this is a temporary branch offset that
		// will be fixed shortly when the total instruction
		// size is known (see below)
		bytecode.add( 0, 0 );

		// Otherwise, load the exception from the execution state and throw it
		int exceptionIndex = getExceptionIndex( mInvocation, frameAfterInvocation );
		bytecode.addAload( localVariables.getArrayRefIndex() );
		bytecode.addLdc( ConstPoolHelper.getIntegerInfo( constPool, exceptionIndex ) );
		bytecode.add( Opcode.AALOAD );
		bytecode.addCheckcast( getJVMClassName( Throwable.class ) );

		branchEntry.setThrownExceptionIndex( bytecode.length() );
		bytecode.add( Opcode.ATHROW );

		// Fix the branch offset from above
		int branchToLocation = bytecode.length();
		int branchOffsest = branchToLocation - branchFromLocation;
		bytecode.write16bit( branchFromLocation + 1, branchOffsest );
	}
	
	private int getExceptionIndex( MethodInvocation mInvocation, Frame adjustedFrame )
	{
		String invokedMethodDescriptor = mInvocation.getFQMethodName().getJVMMethodDescriptor();
		CtClass ctClass = DescriptorWrapper.getReturnType( invokedMethodDescriptor, classPool );
		int exceptionIndex = adjustedFrame.getTopIndex();
		
		if ( ctClass.equals( CtClass.voidType ) && !mInvocation.getFQMethodName().getJVMMethodName().equals( FieldAndMethodConstants.SARASWATI_NEW_MARKER_METHOD ) )
			++exceptionIndex;
			
		return exceptionIndex;
	}

	private void convertWrapperToPrimitive( Type type )
	{
		if ( type.equals( Type.BOOLEAN ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Boolean.class ), BOOLEAN_VALUE_METHOD, getJVMMethodDescriptor( boolean.class ) );
		}
		else if ( type.equals( Type.BYTE ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Byte.class ), BYTE_VALUE_METHOD, getJVMMethodDescriptor( byte.class ) );
		}
		else if ( type.equals( Type.CHAR ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Character.class ), CHAR_VALUE_METHOD, getJVMMethodDescriptor( char.class ) );
		}
		else if ( type.equals( Type.SHORT ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Short.class ), SHORT_VALUE_METHOD, getJVMMethodDescriptor( short.class ) );
		}
		else if ( type.equals( Type.INTEGER ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Integer.class ), INT_VALUE_METHOD, getJVMMethodDescriptor( int.class ) );
		}
		else if ( type.equals( Type.LONG ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Long.class ), LONG_VALUE_METHOD, getJVMMethodDescriptor( long.class ) );
		}
		else if ( type.equals( Type.FLOAT ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Float.class ), FLOAT_VALUE_METHOD, getJVMMethodDescriptor( float.class ) );
		}
		else if ( type.equals( Type.DOUBLE ) )
		{
			bytecode.addInvokevirtual( getJVMClassName( Double.class ), DOUBLE_VALUE_METHOD, getJVMMethodDescriptor( double.class ) );
		}
	}

	private void convertPrimitiveToWrapper( Type type )
	{
		assert ( type != null );
		assert ( PRIMITIVE_TYPES.contains( type ) );

		if ( type.equals( Type.BOOLEAN ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Boolean.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Boolean.class, boolean.class ) );
		}
		else if ( type.equals( Type.BYTE ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Byte.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Byte.class, byte.class ) );
		}
		else if ( type.equals( Type.CHAR ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Character.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Character.class, char.class ) );
		}
		else if ( type.equals( Type.SHORT ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Short.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Short.class, short.class ) );
		}
		else if ( type.equals( Type.INTEGER ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Integer.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Integer.class, int.class ) );
		}
		else if ( type.equals( Type.LONG ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Long.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Long.class, long.class ) );
		}
		else if ( type.equals( Type.FLOAT ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Float.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Float.class, float.class ) );
		}
		else if ( type.equals( Type.DOUBLE ) )
		{
			bytecode.addInvokestatic( getJVMClassName( Double.class ), VALUE_OF_METHOD, getJVMMethodDescriptor( Double.class, double.class ) );
		}
	}

	private int calculateOpStackArraySize( Frame frame )
	{
		int arraySize = 0;

		for ( int i = 0; i <= frame.getTopIndex(); ++i )
		{
			Type type = frame.getStack( i );

			if ( !stackTypeIsValid( type ) )
				continue;

			++arraySize;
		}

		return arraySize;
	}

	private int calculateLocalVariablesArraySize( Frame frame )
	{
		int arraySize = 0;

		for ( int i = 0; i < frame.localsLength(); ++i )
		{
			Type type = frame.getLocal( i );

			if ( !registerTypeIsValid( type ) )
				continue;

			++arraySize;
		}

		return arraySize;
	}

	private boolean stackTypeIsValid( Type type )
	{
		// Note: TOP is the second word position of a double-word type (i.e., long, double)
		if ( type == Type.TOP )
			return false;
		else
			return true;
	}

	private boolean registerTypeIsValid( Type type )
	{
		// Notes:
		// -TOP is the second word position of a double-word type (i.e., long, double)
		// -type == null means that the register is empty at this point in execution
		if ( ( type == Type.TOP ) || ( type == null ) )
			return false;
		else
			return true;
	}
}
