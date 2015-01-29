package ch.shaktipat.exwrapper.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class URLWrapper
{
	public static URL __new( String name )
	{
		try
		{
			return new URL( name );
		}
		catch ( MalformedURLException e )
		{
			throw new MalformedURLExceptionWrapper( e );
		}
	}
	
	public static InputStream openStream( URL url )
	{
		try
		{
			return url.openStream();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
