package ch.shaktipat.exwrapper.java.io;

import java.io.Closeable;
import java.io.IOException;

public class CloseableWrapper
{
	public static void close( Closeable closeable )
	{
		try
		{
			closeable.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
