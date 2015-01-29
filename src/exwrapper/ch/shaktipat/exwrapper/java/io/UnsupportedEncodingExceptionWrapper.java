package ch.shaktipat.exwrapper.java.io;

import java.io.UnsupportedEncodingException;

public class UnsupportedEncodingExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public UnsupportedEncodingExceptionWrapper( UnsupportedEncodingException e )
	{	
		super( e );
	}
}
