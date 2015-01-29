package ch.shaktipat.saraswati.example.volatility.initial;

import java.io.Serializable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class VolatilityInitialDynamicRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatilityInitialDynamicRunnable.class.getName() );

	@Override
	@PersistentMethod( volatility=Volatility.DYNAMIC )
	public void run()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
}
