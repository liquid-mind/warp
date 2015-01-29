package ch.shaktipat.saraswati.internal.engine;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;

import ch.shaktipat.exwrapper.java.io.FileInputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.InputStreamWrapper;
import ch.shaktipat.exwrapper.java.net.URIWrapper;
import ch.shaktipat.exwrapper.org.apache.commons.configuration.XMLConfigurationWrapper;

public class SaraswatiConfiguration
{
	private static final String SARASWATI_HOME;
	private static final String POBJECTS_DIR;
	private static final String USER_DIR;
	private static final String DEBUG_DIR;
	private static final String LOGS_DIR;
	
	static
	{
		SARASWATI_HOME = System.getProperty( "ch.shaktipat.saraswati.saraswatiHome" );
		POBJECTS_DIR = SARASWATI_HOME + "/pobjects";
		USER_DIR = SARASWATI_HOME + "/user";
		DEBUG_DIR = SARASWATI_HOME + "/debug";
		LOGS_DIR = SARASWATI_HOME + "/logs";
	}

	private URL[] userClassPath;
	private int pObjectsThreadPoolSize;
	private String rmiHostName;
	private int rmiPort;
	private String webHostName;
	private int webPort;
	
	public void start()
	{
		setupEmptyDirs();
		Configuration config = getConfiguration();
		setupUserClassPath( config );
		pObjectsThreadPoolSize = config.getInt( "pobjects.thread-pool-size[@value]" );
		rmiHostName = config.getString( "rmi-server.host-name[@value]" );
		rmiPort = config.getInt( "rmi-server.port[@value]" );
		webHostName = config.getString( "web-server.host-name[@value]" );
		webPort = config.getInt( "web-server.port[@value]" );
	}
	
	public void stop()
	{
		userClassPath = null;
	}
	
	private static void setupEmptyDirs()
	{
		File pObjectsDir = new File( POBJECTS_DIR );
		File userDir = new File( USER_DIR );
		File debugDir = new File( DEBUG_DIR );
		
		if ( !pObjectsDir.exists() )
			pObjectsDir.mkdirs();
		
		if ( !userDir.exists() )
			userDir.mkdirs();
		
		if ( !debugDir.exists() )
			debugDir.mkdirs();
	}

	private static Configuration getConfiguration()
	{
		String saraswatiHome = System.getProperty( "ch.shaktipat.saraswati.saraswatiHome" );
		File configFile = new File( saraswatiHome, new File( "conf", "saraswati-config.xml" ).getPath() );
		InputStream configIs = FileInputStreamWrapper.__new( configFile );
		
		XMLConfiguration config = new XMLConfiguration();
		config.setDelimiterParsingDisabled( true );
		XMLConfigurationWrapper.load( config, configIs );
		
		InputStreamWrapper.close( configIs );
		
		return config;
	}
	
	private void setupUserClassPath( Configuration config )
	{
		Collection< File > userJars = FileUtils.listFiles( new File( USER_DIR ), new String[] { "jar" }, true );
		List< File > userJarsAsList = new ArrayList< File >( userJars );
		List< URL > userJarsAsURLList = new ArrayList< URL >();
		
		for ( File userJar : userJarsAsList )
			userJarsAsURLList.add( URIWrapper.toURL( userJar.toURI() ) );
		
		userClassPath = userJarsAsURLList.toArray( new URL[ userJarsAsURLList.size() ] );
	}
	
	public URL[] getUserClasspath()
	{
		return userClassPath;
	}

	public String getDebugDir()
	{
		return DEBUG_DIR;
	}

	public String getLogsDir()
	{
		return LOGS_DIR;
	}

	public String getPObjectsDir()
	{
		return POBJECTS_DIR;
	}

	public int getPObjectsThreadPoolSize()
	{
		return pObjectsThreadPoolSize;
	}

	public String getRmiHostName()
	{
		return rmiHostName;
	}

	public int getRmiPort()
	{
		return rmiPort;
	}

	public String getWebHostName()
	{
		return webHostName;
	}

	public int getWebPort()
	{
		return webPort;
	}
}
