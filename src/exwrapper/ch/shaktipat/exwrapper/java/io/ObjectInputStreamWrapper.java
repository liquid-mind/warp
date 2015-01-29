package ch.shaktipat.exwrapper.java.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import ch.shaktipat.exwrapper.java.lang.ClassNotFoundExceptionWrapper;

public class ObjectInputStreamWrapper
{
	public static ObjectInputStream __new( InputStream os )
	{
		try
		{
			return new ObjectInputStream( os );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static Object readObject( ObjectInputStream is )
	{
		try
		{
			return is.readObject();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}
}
