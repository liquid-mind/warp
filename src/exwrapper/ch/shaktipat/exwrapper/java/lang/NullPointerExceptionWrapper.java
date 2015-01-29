package ch.shaktipat.exwrapper.java.lang;

public class NullPointerExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NullPointerExceptionWrapper( NullPointerException e )
	{
		super( e );
	}
}
