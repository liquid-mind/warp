package ch.shaktipat.saraswati.internal.web.controller.server;

import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;
import ch.shaktipat.saraswati.internal.engine.SaraswatiServer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class ServerControlThread extends Thread
{
	private String command;

	public ServerControlThread( String command )
	{
		super();
		this.command = command;
	}

	@Override
	public void run()
	{
		// Wait for the browser to receive feedback on restart/shutdown before actually
		// shutting down the server.
		// TODO Find a better way to achieve this.
		ThreadWrapper.sleep( 1000 );
		
		if ( command.equals( Controller.RESTART_COMMAND ) )
			SaraswatiServer.restart();
		else if ( command.equals( Controller.SHUTDOWN_COMMAND ) )
			SaraswatiServer.stop();
		else
			throw new RuntimeException( "Unknown command: " + command );
	}
}
