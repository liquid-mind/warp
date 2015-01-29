package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWrapper
{
	public static void write( OutputStream outputStream, int b )
	{
		try
		{
			outputStream.write( b );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void flush( OutputStream outputStream )
	{
		try
		{
			outputStream.flush();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void close( OutputStream outputStream )
	{
		try
		{
			outputStream.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
