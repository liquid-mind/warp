package ch.shaktipat.saraswati.test.pobject.pprocess.volatility;

import java.util.concurrent.ExecutionException;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.volatility.VolatilityException;

public class VolatilityUserSpaceTest
{
	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();
	
	public void testStableToStable() throws ExecutionException, InterruptedException
	{
		Runnable vtRunnable = new VolatilityTestProcess( VolatilityTestType.STABLE_TO_STABLE );
		PProcessHandle vtHandle = pEnvironment.getPProcessManager().create( vtRunnable );
		vtHandle.start();
		vtHandle.join();
		vtHandle.destroy();
	}
	
	public void testVolatileToVolatile() throws ExecutionException, InterruptedException
	{
		Runnable vtRunnable = new VolatilityTestProcess( VolatilityTestType.VOLATILE_TO_VOLATILE );
		PProcessHandle vtHandle = pEnvironment.getPProcessManager().create( vtRunnable );
		vtHandle.start();
		vtHandle.join();
		vtHandle.destroy();
	}
	
	public void testStableToVolatile() throws ExecutionException, InterruptedException
	{
		Runnable vtRunnable = new VolatilityTestProcess( VolatilityTestType.STABLE_TO_VOLATILE );
		PProcessHandle vtHandle = pEnvironment.getPProcessManager().create( vtRunnable );
		vtHandle.start();
		vtHandle.join();
		vtHandle.destroy();
	}
	
	public void testVolatileToStable() throws ExecutionException, InterruptedException
	{
		Runnable vtRunnable = new VolatilityTestProcess( VolatilityTestType.VOLATILE_TO_STABLE );
		PProcessHandle vtHandle = pEnvironment.getPProcessManager().create( vtRunnable );
		vtHandle.start();
		
		try
		{
			vtHandle.getFuture().get();
		}
		catch ( ExecutionException e )
		{
			Throwable t = e.getCause();
			
			if ( t instanceof VolatilityException )
				return;
		}

		vtHandle.destroy();
		
		throw new RuntimeException( "Expected VolatilityException but none was caught." );
	}
	
	public void testVolatileToStableCatchException() throws ExecutionException, InterruptedException
	{
		Runnable vtRunnable = new VolatilityTestProcess( VolatilityTestType.VOLATILE_TO_STABLE_CATCH_EXCEPTION );
		PProcessHandle vtHandle = pEnvironment.getPProcessManager().create( vtRunnable );
		vtHandle.start();
		vtHandle.getFuture().get();
		vtHandle.destroy();
	}
}
