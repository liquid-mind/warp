package ch.shaktipat.exwrapper.java.net;

import java.net.MalformedURLException;

public class MalformedURLExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public MalformedURLExceptionWrapper( MalformedURLException e )
	{
		super( e );
	}
}
