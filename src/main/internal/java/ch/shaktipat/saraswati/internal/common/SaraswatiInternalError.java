package ch.shaktipat.saraswati.internal.common;

public class SaraswatiInternalError extends Error
{
	private static final long serialVersionUID = 1L;

	public SaraswatiInternalError()
	{
		super();
	}

	public SaraswatiInternalError( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
	{
		super( message, cause, enableSuppression, writableStackTrace );
	}

	public SaraswatiInternalError( String message, Throwable cause )
	{
		super( message, cause );
	}

	public SaraswatiInternalError( String message )
	{
		super( message );
	}

	public SaraswatiInternalError( Throwable cause )
	{
		super( cause );
	}
}
