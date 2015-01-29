package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.Reader;

public class ReaderWrapper
{
	public static void close( Reader reader )
	{
		try
		{
			reader.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
