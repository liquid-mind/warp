package ch.shaktipat.saraswati.example.volatility.initial;

import java.io.IOException;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.volatility.Volatility;

public class VolatilityInitialClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
	
		runWithDefaultInitialVolatility( pProcessManager );
		runWithSpecificInitialVolatility( pProcessManager );
		runWithInheritedInitialVolatility( pProcessManager );
	}
	
	private static void runWithDefaultInitialVolatility( PProcessManager pProcessManager )
	{
		PProcessHandle handle = pProcessManager.create( new VolatilityInitialDynamicRunnable(), "Default Initial Volatility" );
		handle.start();
		handle.join();
	}
	
	private static void runWithSpecificInitialVolatility( PProcessManager pProcessManager )
	{
		PProcessHandle handle = pProcessManager.create( new VolatilityInitialDynamicRunnable(), "Specific Initial Volatility" );
		handle.setInitialVolatility( Volatility.VOLATILE );
		handle.start();
		handle.join();
	}
	
	private static void runWithInheritedInitialVolatility( PProcessManager pProcessManager )
	{
		PProcessHandle handle = pProcessManager.create( new VolatilityInitialStableRunnable(), "Initial Method, Initial Volatility" );
		handle.start();
		handle.join();
	}
}
