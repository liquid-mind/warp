package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper
{
	public static int read( InputStream inputStream )
	{
		try
		{
			return inputStream.read();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void close( InputStream inputStream )
	{
		try
		{
			inputStream.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
