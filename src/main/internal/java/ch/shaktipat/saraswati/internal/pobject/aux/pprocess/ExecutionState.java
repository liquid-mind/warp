package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;

public class ExecutionState implements Serializable
{
	private static final long serialVersionUID = 1L;

	// For non-static invocations this field denotes
	// the object associated with the invocation
	private Object associatedObject;

	// Intermediate storage area for the local variables
	// of an instrumented method.
	private Object[] localVariables;

	// Intermediate storage area for the operand stack
	// of an instrumented method.
	private Object[] operandStack;

	// Intermediate storage area for the "program counter"
	// of an instrumented method. This isn't the actual
	// Java VM program counter but rather a unique id
	// used by the instrumented method to decide where to
	// branch to when (re-)invoked.
	private int programCounter;

	public ExecutionState()
	{
		super();
	}

	public ExecutionState( Object associatedObject, Object[] localVariables, Object[] operandStack, int programCounter )
	{
		super();
		this.associatedObject = associatedObject;
		this.localVariables = localVariables;
		this.operandStack = operandStack;
		this.programCounter = programCounter;
	}

	public Object getAssociatedObject()
	{
		return associatedObject;
	}

	public void setAssociatedObject( Object associatedObject )
	{
		this.associatedObject = associatedObject;
	}

	public Object[] getLocalVariables()
	{
		return localVariables;
	}

	public void setLocalVariables( Object[] localVariables )
	{
		this.localVariables = localVariables;
	}

	public Object[] getOperandStack()
	{
		return operandStack;
	}

	public void setOperandStack( Object[] operandStack )
	{
		this.operandStack = operandStack;
	}

	public int getProgramCounter()
	{
		return programCounter;
	}

	public void setProgramCounter( int programCounter )
	{
		this.programCounter = programCounter;
	}
}
