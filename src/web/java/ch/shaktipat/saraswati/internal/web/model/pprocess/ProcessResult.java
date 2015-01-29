package ch.shaktipat.saraswati.internal.web.model.pprocess;

public class ProcessResult
{
	private Object retVal;
	private Throwable exception;
	
	public Object getRetVal()
	{
		return retVal;
	}

	public void setRetVal( Object retVal )
	{
		this.retVal = retVal;
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
