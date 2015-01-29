package ch.shaktipat.exwrapper.org.apache.commons.lang.reflect;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.reflect.ConstructorUtils;

import ch.shaktipat.exwrapper.java.lang.IllegalAccessExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.InstantiationExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.NoSuchMethodExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.InvocationTargetExceptionWrapper;

public class ConstructorUtilsWrapper
{
	public static Object invokeConstructor( Class< ? > theClass, Object ... args ) 
	{
		try
		{
			return ConstructorUtils.invokeConstructor( theClass, args );
		}
		catch ( NoSuchMethodException e )
		{
			throw new NoSuchMethodExceptionWrapper( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new IllegalAccessExceptionWrapper( e );
		}
		catch ( InvocationTargetException e )
		{
			throw new InvocationTargetExceptionWrapper( e );
		}
		catch ( InstantiationException e )
		{
			throw new InstantiationExceptionWrapper( e );
		}
	}
}
