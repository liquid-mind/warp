package ch.shaktipat.saraswati.internal.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class ClassLoaderAwareObjectInputStream extends ObjectInputStream
{
	public static ClassLoaderAwareObjectInputStream create( InputStream is )
	{
		try
		{
			return new ClassLoaderAwareObjectInputStream( is );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public ClassLoaderAwareObjectInputStream( InputStream is ) throws IOException
	{
		super( is );
	}

	@Override
	protected Class< ? > resolveClass( ObjectStreamClass osClass ) throws IOException, ClassNotFoundException
	{
		Class< ? > resolvedClass = null;
		
		String className = osClass.getName();
		resolvedClass = Class.forName( className, false, Thread.currentThread().getContextClassLoader() );
		
		return resolvedClass;
	}

}
