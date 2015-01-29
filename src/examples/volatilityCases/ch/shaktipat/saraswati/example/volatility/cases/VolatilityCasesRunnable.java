package ch.shaktipat.saraswati.example.volatility.cases;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;

@PersistentClass
public class VolatilityCasesRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatilityCasesRunnable.class.getName() );
	
	private PProcess currentProcess;
	
	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void run()
	{
		currentProcess = PProcessStatic.currentProcess();
		invokeStableFromStable();
		invokeVolatileFromStable();
		invokeStableFromVolatile();
		invokeVolatileFromVolatile();
	}

	@PersistentMethod( volatility=Volatility.STABLE )
	private void invokeStableFromStable()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
		stableMethod();
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	private void invokeVolatileFromStable()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
		volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void invokeStableFromVolatile()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
		
		try
		{
			stableMethod();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void invokeVolatileFromVolatile()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
		volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	private void stableMethod()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void volatileMethod()
	{
		logger.info( "Volatility=" + currentProcess.getVolatility() );
	}
}
