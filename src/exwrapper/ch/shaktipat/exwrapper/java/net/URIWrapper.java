package ch.shaktipat.exwrapper.java.net;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URIWrapper
{
	public static URI __new( String name )
	{
		try
		{
			return new URI( name );
		}
		catch ( URISyntaxException e )
		{
			throw new URISyntaxExceptionWrapper( e );
		}
	}
	
	public static URL toURL( URI uri )
	{
		try
		{
			return uri.toURL();
		}
		catch ( MalformedURLException e )
		{
			throw new MalformedURLExceptionWrapper( e );
		}
	}
}
