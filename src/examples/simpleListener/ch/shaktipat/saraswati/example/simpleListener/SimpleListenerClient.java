package ch.shaktipat.saraswati.example.simpleListener;

import java.io.IOException;

import ch.shaktipat.saraswati.common.BasicEvent;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class SimpleListenerClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
		
		if ( args[ 0 ].equals( "startProcess" ) )
		{
			PProcessHandle handle = pProcessManager.create( new SimpleListenerRunnable( "hello" ), "Simple Listener Process" );
			handle.start();
		}
		else
		{
			PProcessHandle handle = pProcessManager.findByName( "Simple Listener Process" ).get( 0 );
			BasicEvent event = new BasicEvent();
			event.setProperty( "My Property", " world!" );
			handle.send( event );
//			handle.join();
		}
	}
}
