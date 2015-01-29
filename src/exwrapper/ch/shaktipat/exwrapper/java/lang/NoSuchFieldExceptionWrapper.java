package ch.shaktipat.exwrapper.java.lang;

public class NoSuchFieldExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NoSuchFieldExceptionWrapper( NoSuchFieldException e )
	{
		super( e );
	}
}
