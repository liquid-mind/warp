package ch.shaktipat.exwrapper.java.lang;

public class InstantiationExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InstantiationExceptionWrapper( InstantiationException e )
	{
		super( e );
	}
}
