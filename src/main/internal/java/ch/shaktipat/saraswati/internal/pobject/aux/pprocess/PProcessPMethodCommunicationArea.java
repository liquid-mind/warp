package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;

import ch.shaktipat.saraswati.internal.instrument.method.MethodInvocationType;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;

public class PProcessPMethodCommunicationArea implements Serializable
{
	// Container to invoked method: isContainerInvocation, exceptionOccured
	// Invoked method to container: invokedMethod, methodInvocationType
	
	private static final long serialVersionUID = 1L;

	// Used by instrumented methods to inform the
	// container as to which method should be invoked
	private FQJVMMethodName invokedMethodName;
	
	private MethodInvocationType methodInvocationType;

	// Set by the container to true when an instrumented
	// method throws an exception. Used by subsequent
	// instrumented methods to decide whether or not to
	// rethrow the exception.
	private boolean exceptionOccured;

	// Set by the container to inform the instrumented
	// code that it is actually being invoked by the
	// container. This is necessary since a non-instrumented
	// class (i.e., a library class) could otherwise
	// directly invoke an instrumented method, which
	// would be illegal. If this flag is false when
	// an instrumented method is invoked, that method
	// will throw an exception.
	private boolean isContainerInvocation;

	public PProcessPMethodCommunicationArea()
	{
		super();
		invokedMethodName = new FQJVMMethodName();
		exceptionOccured = false;
		isContainerInvocation = false;
	}

	public PProcessPMethodCommunicationArea( FQJVMMethodName invokedMethodName, boolean exceptionOccured, boolean isContainerInvocation )
	{
		super();
		this.invokedMethodName = invokedMethodName;
		this.exceptionOccured = exceptionOccured;
		this.isContainerInvocation = isContainerInvocation;
	}

	public FQJVMMethodName getInvokedMethodName()
	{
		return invokedMethodName;
	}

	public void setInvokedMethodName( FQJVMMethodName invokedMethodName )
	{
		this.invokedMethodName = invokedMethodName;
	}

	public boolean getExceptionOccured()
	{
		return exceptionOccured;
	}

	public void setExceptionOccured( boolean exceptionOccured )
	{
		this.exceptionOccured = exceptionOccured;
	}

	public boolean getIsContainerInvocation()
	{
		return isContainerInvocation;
	}

	public void setIsContainerInvocation( boolean isContainerInvocation )
	{
		this.isContainerInvocation = isContainerInvocation;
	}

	public MethodInvocationType getMethodInvocationType()
	{
		return methodInvocationType;
	}

	public void setMethodInvocationType( MethodInvocationType methodInvocationType )
	{
		this.methodInvocationType = methodInvocationType;
	}

}
