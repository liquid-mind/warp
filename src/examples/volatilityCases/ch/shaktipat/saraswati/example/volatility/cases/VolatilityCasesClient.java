package ch.shaktipat.saraswati.example.volatility.cases;

import java.io.IOException;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class VolatilityCasesClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
		
		PProcessHandle handle = pProcessManager.create( new VolatilityCasesRunnable(), "Volatility Cases Process" );
		handle.start();
		handle.join();
	}
}
