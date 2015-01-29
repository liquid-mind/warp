package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;

public class MethodResultInstance implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Object returnValue;
	private Throwable exception;
	
	// Signalizes whether this method requested for another method to
	// be invoked or not.
	private boolean invokedOtherMethod;
	
	public MethodResultInstance()
	{
		super();
	}
	
	public MethodResultInstance( Object returnValue, Throwable exception )
	{
		super();
		this.returnValue = returnValue;
		this.exception = exception;
	}
	
	public MethodResultInstance( Object returnValue, Throwable exception, boolean invokedOtherMethod )
	{
		super();
		this.returnValue = returnValue;
		this.exception = exception;
		this.invokedOtherMethod = invokedOtherMethod;
	}

	public Object getReturnValue()
	{
		return returnValue;
	}

	public void setReturnValue( Object returnValue )
	{
		this.returnValue = returnValue;
	}

	public Throwable getException()
	{
		return exception;
	}

	public void setException( Throwable exception )
	{
		this.exception = exception;
	}

	public boolean getInvokedOtherMethod()
	{
		return invokedOtherMethod;
	}

	public void setInvokedOtherMethod( boolean invokedOtherMethod )
	{
		this.invokedOtherMethod = invokedOtherMethod;
	}

	@Override
	public String toString()
	{
		return "MethodResultInstance [returnValue=" + returnValue + ", exception=" + exception + ", invokedOtherMethod=" + invokedOtherMethod + "]";
	}

	
}
