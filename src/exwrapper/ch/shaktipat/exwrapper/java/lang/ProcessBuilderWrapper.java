package ch.shaktipat.exwrapper.java.lang;

import java.io.IOException;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class ProcessBuilderWrapper
{
	public static Process start( ProcessBuilder builder )
	{
		try
		{
			return builder.start();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
