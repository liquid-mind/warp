package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.Writer;

public class WriterWrapper
{
	public static void write( Writer writer, String theString )
	{
		try
		{
			writer.write( theString );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void close( Writer writer )
	{
		try
		{
			writer.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
