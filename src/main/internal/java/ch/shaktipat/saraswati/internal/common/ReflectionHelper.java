package ch.shaktipat.saraswati.internal.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectionHelper
{
	public static Method getMethod( Class< ? > theClass, String methodName, Class< ? >[] paramTypes )
	{
		Method method = null;
		
		try
		{
			method = theClass.getDeclaredMethod( methodName, paramTypes );
			method.setAccessible( true );
		}
		catch ( NoSuchMethodException | SecurityException e )
		{
		}
		
		if ( method == null )
		{
			Class< ? > superClasss = theClass.getSuperclass();
			
			if ( superClasss != null )
				method = getMethod( superClasss, methodName, paramTypes );
		}
		
		return method;
	}
	
	public static Constructor< ? > getConstructor( Class< ? > theClass, Class< ? >[] paramTypes )
	{
		Constructor< ? > constructor = null;
		
		try
		{
			constructor = theClass.getDeclaredConstructor( paramTypes );
			constructor.setAccessible( true );
		}
		catch ( NoSuchMethodException | SecurityException e )
		{
		}
		
		if ( constructor == null )
		{
			Class< ? > superClasss = theClass.getSuperclass();
			
			if ( superClasss != null )
				constructor = getConstructor( superClasss, paramTypes );
		}
		
		return constructor;
	}
}
