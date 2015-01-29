package ch.shaktipat.exwrapper.java.net;

import java.net.URISyntaxException;

public class URISyntaxExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public URISyntaxExceptionWrapper( URISyntaxException e )
	{
		super( e );
	}
}
