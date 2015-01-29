package ch.shaktipat.saraswati.internal.instrument.method;

import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import ch.shaktipat.exwrapper.javassist.bytecode.CodeAttributeWrapper;

public class LocalVariables
{
	// reference to the (container / method) communication area)
	private int comAreaRefIndex;
	// reference to the execution state
	private int executionStateRefIndex;
	// reference to the method to be invoked
	private int invokedMethodRefIndex;
	// reference to the current StackTraceElement
	private int stackTraceElemIndex;
	// value of the program counter
	private int programCounterIndex;
	// value of either a local variable or stack item
	private int executionStateValueIndex;
	// reference to an array
	private int arrayRefIndex;
	// index into the array
	private int arrayCounterIndex;
	
	public LocalVariables( CtMethod ctMethod )
	{
		CodeAttribute codeAttribute = ctMethod.getMethodInfo().getCodeAttribute();

		// TBD: make sure that we are not overflowing the
		// local variable array (max locals variables ==
		// max of unsigned short? i.e. 2^16?)

		comAreaRefIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, comAreaRefIndex, 1 );
		executionStateRefIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, executionStateRefIndex, 1 );
		invokedMethodRefIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, invokedMethodRefIndex, 1 );
		stackTraceElemIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, stackTraceElemIndex, 1 );
		arrayRefIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, arrayRefIndex, 1 );
		arrayCounterIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, arrayCounterIndex, 1 );
		programCounterIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, programCounterIndex, 1 );

		// Note: this variable takes up two positions in the
		// local variable array to ensure space for floats / doubles,
		// however for all other types only the first position
		// is actually used.
		executionStateValueIndex = codeAttribute.getMaxLocals();
		CodeAttributeWrapper.insertLocalVar( codeAttribute, executionStateValueIndex, 2 );
	}

	public int geStackTraceElemIndex()
	{
		return stackTraceElemIndex;
	}

	public int getComAreaRefIndex()
	{
		return comAreaRefIndex;
	}

	public int getArrayRefIndex()
	{
		return arrayRefIndex;
	}

	public int getArrayCounterIndex()
	{
		return arrayCounterIndex;
	}

	public int getProgramCounterIndex()
	{
		return programCounterIndex;
	}

	public int getExecutionStateValueIndex()
	{
		return executionStateValueIndex;
	}

	public int getExecutionStateRefIndex()
	{
		return executionStateRefIndex;
	}

	public int getInvokedMethodRefIndex()
	{
		return invokedMethodRefIndex;
	}

	@Override
	public String toString()
	{
		String stringValue = this.getClass().getName() + "@" + this.hashCode() + System.lineSeparator();
		
		stringValue += "{" + System.lineSeparator();
		
		stringValue += "\tregister " + comAreaRefIndex + ": comAreaRefIndex" + System.lineSeparator();
		stringValue += "\tregister " + executionStateRefIndex + ": executionStateRefIndex" + System.lineSeparator();
		stringValue += "\tregister " + invokedMethodRefIndex + ": invokedMethodRefIndex" + System.lineSeparator();
		stringValue += "\tregister " + stackTraceElemIndex + ": stackTraceElemIndex" + System.lineSeparator();
		stringValue += "\tregister " + arrayRefIndex + ": arrayRefDim1Index" + System.lineSeparator();
		stringValue += "\tregister " + arrayCounterIndex + ": arrayDim1CounterIndex" + System.lineSeparator();
		stringValue += "\tregister " + programCounterIndex + ": programCounterIndex" + System.lineSeparator();
		stringValue += "\tregister " + executionStateValueIndex + ": executionStateValueIndex" + System.lineSeparator();
		
		stringValue += "}" + System.lineSeparator();
		
		return stringValue;
	}
}
