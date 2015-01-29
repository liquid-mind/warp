package ch.shaktipat.exwrapper.java.lang;

public class ClassLoaderWrapper
{
	public static Class< ? > loadClass( ClassLoader loader, String name )
	{
		try
		{
			return loader.loadClass( name );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}
}
