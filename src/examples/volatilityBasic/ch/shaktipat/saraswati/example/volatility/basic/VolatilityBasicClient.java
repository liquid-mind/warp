package ch.shaktipat.saraswati.example.volatility.basic;

import java.io.IOException;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class VolatilityBasicClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
	
		PProcessHandle handle = pProcessManager.create( new VolatilityBasicRunnable( args[ 0 ] ), "Volatility Basic Process" );
		handle.start();
		handle.join();
	}
}
