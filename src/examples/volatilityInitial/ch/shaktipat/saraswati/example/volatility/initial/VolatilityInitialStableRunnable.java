package ch.shaktipat.saraswati.example.volatility.initial;

import java.io.Serializable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class VolatilityInitialStableRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatilityInitialStableRunnable.class.getName() );

	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void run()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
		
		PProcessManager pProcessManager = PEnvironmentFactory.createLocal().getPProcessManager();
		PProcessHandle handle = pProcessManager.create( new VolatilityInitialDynamicRunnable(), "Inherited Initial Volatility" );
		handle.start();
		handle.join();
	}
}
