package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;

public class FileNotFoundExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public FileNotFoundExceptionWrapper( IOException e )
	{
		super( e );
	}
}
