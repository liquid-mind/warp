package ch.shaktipat.exwrapper.java.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassWrapper
{
	public static Class< ? > forName( String name, boolean initialize, ClassLoader loader )
	{
		try
		{
			return Class.forName( name, initialize, loader );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}

	public static Class< ? > forName( String name )
	{
		try
		{
			return Class.forName( name );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}

	public static Object newInstance( Class< ? > theClass )
	{
		try
		{
			return theClass.newInstance();
		}
		catch ( InstantiationException e )
		{
			throw new InstantiationExceptionWrapper( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new IllegalAccessExceptionWrapper( e );
		}
	}

	public static Method getMethod( Class< ? > theClass, String name, Class< ? >... parameterTypes )
	{
		try
		{
			return theClass.getMethod( name, parameterTypes );
		}
		catch ( NoSuchMethodException e )
		{
			throw new NoSuchMethodExceptionWrapper( e );
		}
		catch ( SecurityException e )
		{
			throw new SecurityExceptionWrapper( e );
		}
	}

	public static Field getField( Class< ? > theClass, String name )
	{
		try
		{
			return theClass.getField( name );
		}
		catch ( NoSuchFieldException e )
		{
			throw new NoSuchFieldExceptionWrapper( e );
		}
		catch ( SecurityException e )
		{
			throw new SecurityExceptionWrapper( e );
		}
	}

	public static Method getDeclaredMethod( Class< ? > theClass, String name, Class< ? >... parameterTypes )
	{
		try
		{
			return theClass.getDeclaredMethod( name, parameterTypes );
		}
		catch ( NoSuchMethodException e )
		{
			throw new NoSuchMethodExceptionWrapper( e );
		}
		catch ( SecurityException e )
		{
			throw new SecurityExceptionWrapper( e );
		}
	}

	public static Constructor< ? > getDeclaredConstructor( Class< ? > theClass, Class< ? >... parameterTypes )
	{
		try
		{
			return theClass.getDeclaredConstructor( parameterTypes );
		}
		catch ( NoSuchMethodException e )
		{
			throw new NoSuchMethodExceptionWrapper( e );
		}
		catch ( SecurityException e )
		{
			throw new SecurityExceptionWrapper( e );
		}
	}

	public static Field getDeclaredField( Class< ? > theClass, String name )
	{
		try
		{
			return theClass.getDeclaredField( name );
		}
		catch ( NoSuchFieldException e )
		{
			throw new NoSuchFieldExceptionWrapper( e );
		}
		catch ( SecurityException e )
		{
			throw new SecurityExceptionWrapper( e );
		}
	}

}
