package ch.shaktipat.exwrapper.java.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.shaktipat.exwrapper.java.lang.IllegalAccessExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.IllegalArgumentExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.InstantiationExceptionWrapper;

public class ConstructorWrapper
{
	public static Object newInstance( Constructor< ? > constructor, Object... initargs )
	{
		try
		{
			return constructor.newInstance( initargs );
		}
		catch( InstantiationException e )
		{
			throw new InstantiationExceptionWrapper( e );
		}
		catch( IllegalAccessException e )
		{
			throw new IllegalAccessExceptionWrapper( e );
		}
		catch( IllegalArgumentException e )
		{
			throw new IllegalArgumentExceptionWrapper( e );
		}
		catch( InvocationTargetException e )
		{
			throw new InvocationTargetExceptionWrapper( e );
		}
	}
}
