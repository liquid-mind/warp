package ch.shaktipat.saraswati.internal.web;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.engine.SaraswatiConfiguration;

public class SaraswatiWebServer
{
	private static final Logger logger = Logger.getLogger( SaraswatiWebServer.class.getName() );

	private static Server webUIServer;
	
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
	
	private static void startWoExceptionHandling() throws Exception
	{
		SaraswatiConfiguration config = PEngine.getPEngine().getSaraswatiConfiguration();
		webUIServer = new Server( new InetSocketAddress( config.getWebHostName(), config.getWebPort() ) );

		// Setup session ID manager.
		webUIServer.setSessionIdManager( new HashSessionIdManager() );
		
		// Setup context.
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath( "/" );
		ServletHolder holder = handler.addServlet( SaraswatiServlet.class, "/*" );
		holder.setInitOrder( 0 );
		webUIServer.setHandler( handler );

		// Setup session handler.
        HashSessionManager manager = new HashSessionManager();
        SessionHandler sessionHandler = new SessionHandler(manager);
        handler.setHandler(sessionHandler);	
		
        // Start the server.
		webUIServer.start();
	}
	
	public static void stop()
	{
		try
		{
			webUIServer.stop();
		}
		catch ( Exception e )
		{
			logger.log( Level.SEVERE, "Exception occcured while trying to stop web services.", e );
		}
	}
}
