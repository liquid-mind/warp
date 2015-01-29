package ch.shaktipat.exwrapper.java.lang;

public class InterruptedExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InterruptedExceptionWrapper( InterruptedException e )
	{
		super( e );
	}
}
