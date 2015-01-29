package ch.shaktipat.saraswati.example.helloWorld;

import java.io.IOException;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class HelloWorldClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
		
		PProcessHandle handle = pProcessManager.create( new HelloWorldRunnable(), "Hellow World Process" );
		handle.start();
		handle.join();
	}
}
