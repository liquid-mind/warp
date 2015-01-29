package ch.shaktipat.exwrapper.java.lang;

public class NoSuchMethodExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NoSuchMethodExceptionWrapper( NoSuchMethodException e )
	{
		super( e );
	}
}
