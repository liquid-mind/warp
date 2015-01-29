package ch.shaktipat.exwrapper.java.lang.reflect;

import java.lang.reflect.Field;

import ch.shaktipat.exwrapper.java.lang.IllegalAccessExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.IllegalArgumentExceptionWrapper;

public class FieldWrapper
{
	public static void set( Field field, Object obj, Object value )
	{
		try
		{
			field.set( obj, value );
		}
		catch( IllegalAccessException e )
		{
			throw new IllegalAccessExceptionWrapper( e );
		}
		catch( IllegalArgumentException e )
		{
			throw new IllegalArgumentExceptionWrapper( e );
		}
	}
	
	public static Object get( Field field, Object obj )
	{
		try
		{
			return field.get( obj );
		}
		catch( IllegalAccessException e )
		{
			throw new IllegalAccessExceptionWrapper( e );
		}
		catch( IllegalArgumentException e )
		{
			throw new IllegalArgumentExceptionWrapper( e );
		}
	}
}
