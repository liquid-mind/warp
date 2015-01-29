package ch.shaktipat.saraswati.internal.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.engine.SaraswatiConfiguration;

public class SaraswatiRMIServer
{
	private static final Logger logger = Logger.getLogger( SaraswatiRMIServer.class.getName() );

	private static Registry registry;
	
	public static final String BIND_ROOT;
	public static final String SARASWATI_RMI_SERVICE;
	
	static
	{
		SaraswatiConfiguration config = PEngine.getPEngine().getSaraswatiConfiguration();
		String hostName = config.getRmiHostName();
		int port = config.getRmiPort();
		
		BIND_ROOT = "//" + hostName + ":" + port + "/";
		SARASWATI_RMI_SERVICE = BIND_ROOT + SaraswatiRMIService.class.getName();
	}
	
	// TODO probably remove this (also check web server)
	public static void main( String[] args )
	{
		start();
	}
	
	public static void start()
	{
		try
		{
			startWoExceptionHandling();
		}
		catch ( Exception e )
		{
			logger.log( Level.SEVERE, "Exception occcured while trying to start web services.", e );
		}
	}
	
	// TODO find out if there is any way to have the RMI registry
	// bind to a particular host name (currently binding to "*").
	private static void startWoExceptionHandling() throws Exception
	{
		System.setProperty( "java.rmi.server.useCodebaseOnly", "true" );
		System.setProperty( "java.rmi.server.RMIClassLoaderSpi", SaraswatiRMIClassLoaderSpi.class.getName() );
		
		SaraswatiConfiguration config = PEngine.getPEngine().getSaraswatiConfiguration();
		int port = config.getRmiPort();
		registry = LocateRegistry.createRegistry( port );
		Naming.rebind( SARASWATI_RMI_SERVICE, new SaraswatiRMIServiceImpl() );
	}
	
	public static void stop()
	{
		try
		{
			Naming.unbind( SARASWATI_RMI_SERVICE );
			UnicastRemoteObject.unexportObject( registry, true );
		}
		catch ( Exception e )
		{
			logger.log( Level.SEVERE, "Exception occcured while trying to stop web services.", e );
		}
	}
}
