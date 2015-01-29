package ch.shaktipat.exwrapper.java.io;

import java.io.BufferedReader;
import java.io.IOException;

public class BufferedReaderWrapper
{
	public static String readLine( BufferedReader reader )
	{
		try
		{
			return reader.readLine();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
