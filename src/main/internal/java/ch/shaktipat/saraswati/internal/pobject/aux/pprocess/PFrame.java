package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;

import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.volatility.Volatility;

public class PFrame implements Serializable
{
	private static final long serialVersionUID = 1L;

	// The execution state of the method associated with this frame.
	private ExecutionState executionState;
	
	// The method invocation instance that caused this frame to be
	// instantiated (i.e., that caused the associated method to be invoked)
	// We have to remember this to ensure that the persistent process
	// can be interrupted and resumed at any time, i.e., in the middle
	// of executing a method.
	private MethodInvocationInstance methodInvocationInstance;
	
	// To be used in case a stack trace is needed; mainly, when an exception is thrown.
	private StackTraceElement stackTraceElement;
	
	private Volatility volatility;

	public PFrame( MethodInvocationInstance methodInvocationInstance, StackTraceElement stackTraceElement, Volatility topFrameVolatility )
	{
		this.methodInvocationInstance = methodInvocationInstance;
		this.stackTraceElement = stackTraceElement;
		executionState = new ExecutionState();
		volatility = determineVolatility( topFrameVolatility );
	}
	
	private Volatility determineVolatility( Volatility topFrameVolatility )
	{
		FQJVMMethodName unresolvedFqMethodName = methodInvocationInstance.getFQMethodName();
		FQJVMMethodName resolvedFqMethodName = methodInvocationInstance.getResolvedFqMethodName();
		FQJVMMethodName partiallyResolvedFqMethodName = new FQJVMMethodName(
			resolvedFqMethodName.getJVMClassName(),
			unresolvedFqMethodName.getJVMMethodName(),
			unresolvedFqMethodName.getJVMMethodDescriptor() );
		Volatility declaredVolatility = PEngine.getPEngine().getPClassLoader().getSymbolRegistry().getMethodVolatility( partiallyResolvedFqMethodName );
		
		Volatility actualVolatility = null;
		
		if ( declaredVolatility.equals( Volatility.DYNAMIC ) )
			actualVolatility = topFrameVolatility;
		else
			actualVolatility = declaredVolatility;
		
		return actualVolatility;
	}

	public ExecutionState getExecutionState()
	{
		return executionState;
	}

	public void setExecutionState( ExecutionState executionState )
	{
		this.executionState = executionState;
	}

	public MethodInvocationInstance getMethodInvocationInstance()
	{
		return methodInvocationInstance;
	}

	public void setMethodInvocationInstance( MethodInvocationInstance methodInvocationInstance )
	{
		this.methodInvocationInstance = methodInvocationInstance;
	}

	public StackTraceElement getStackTraceElement()
	{
		return stackTraceElement;
	}

	public void setStackTraceElement( StackTraceElement stackTraceElement )
	{
		this.stackTraceElement = stackTraceElement;
	}

	public Volatility getVolatility()
	{
		return volatility;
	}
}
