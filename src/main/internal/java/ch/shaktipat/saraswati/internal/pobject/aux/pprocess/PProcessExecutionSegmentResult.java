package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;

public class PProcessExecutionSegmentResult implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Object returnValue;
	private Throwable exception;
	
	public PProcessExecutionSegmentResult()
	{
		super();
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
}
