package ch.shaktipat.exwrapper.java.lang;

public class SecurityExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public SecurityExceptionWrapper( SecurityException e )
	{
		super( e );
	}
}
