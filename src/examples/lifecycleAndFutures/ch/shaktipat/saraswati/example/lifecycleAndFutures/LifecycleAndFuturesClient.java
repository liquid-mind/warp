package ch.shaktipat.saraswati.example.lifecycleAndFutures;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class LifecycleAndFuturesClient
{
	public static void main( String[] args ) throws InterruptedException, ExecutionException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
		
		PProcessHandle handle = pProcessManager.create( new LifecycleAndFuturesRunnable(), "Lifecycle and Futures Process" );
		handle.start();
		Future< String > future = handle.getFuture();
		System.out.println( future.get() );
		handle.destroy();
	}
}
