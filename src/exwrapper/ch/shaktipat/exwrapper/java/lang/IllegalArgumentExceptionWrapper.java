package ch.shaktipat.exwrapper.java.lang;

public class IllegalArgumentExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public IllegalArgumentExceptionWrapper( IllegalArgumentException e )
	{
		super( e );
	}
}
