package ch.shaktipat.exwrapper.org.eclipse.jetty.util.resource;

import java.io.IOException;

import org.eclipse.jetty.util.resource.Resource;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class ResourceWrapper
{
	public static Resource newResource( String resourceName )
	{
		try
		{
			return Resource.newResource( resourceName );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static Resource addPath( Resource resource, String resourceName )
	{
		try
		{
			return resource.addPath( resourceName );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
