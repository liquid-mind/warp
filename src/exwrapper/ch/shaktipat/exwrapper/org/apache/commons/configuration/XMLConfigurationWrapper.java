package ch.shaktipat.exwrapper.org.apache.commons.configuration;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class XMLConfigurationWrapper
{
	public static XMLConfiguration __new( File name )
	{
		try
		{
			return new XMLConfiguration( name );
		}
		catch ( ConfigurationException e )
		{
			throw new ConfigurationExceptionWrapper( e );
		}
	}
	
	public static void load( XMLConfiguration obj, InputStream in )
	{
		try
		{
			obj.load( in );
		}
		catch ( ConfigurationException e )
		{
			throw new ConfigurationExceptionWrapper( e );
		}
	}
}
