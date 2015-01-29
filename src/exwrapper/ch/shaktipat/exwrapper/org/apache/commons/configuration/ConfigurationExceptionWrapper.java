package ch.shaktipat.exwrapper.org.apache.commons.configuration;

import org.apache.commons.configuration.ConfigurationException;

public class ConfigurationExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ConfigurationExceptionWrapper( ConfigurationException e )
	{
		super( e );
	}
}
