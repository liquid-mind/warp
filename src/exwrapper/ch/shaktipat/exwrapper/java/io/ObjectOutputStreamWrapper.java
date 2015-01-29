package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectOutputStreamWrapper
{
	public static ObjectOutputStream __new( OutputStream os )
	{
		try
		{
			return new ObjectOutputStream( os );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void writeObject( ObjectOutputStream os, Object obj )
	{
		try
		{
			os.writeObject( obj );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void flush( ObjectOutputStream os )
	{
		try
		{
			os.flush();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
