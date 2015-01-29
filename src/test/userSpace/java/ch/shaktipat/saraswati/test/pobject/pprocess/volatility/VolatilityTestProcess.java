package ch.shaktipat.saraswati.test.pobject.pprocess.volatility;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;

@PersistentClass
public class VolatilityTestProcess implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;

	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();
	
	private VolatilityTestType testType;
	
	public VolatilityTestProcess( VolatilityTestType testType )
	{
		super();
		this.testType = testType;
	}

	@Override
	public void run()
	{
		if ( testType.equals( VolatilityTestType.STABLE_TO_STABLE ) )
			testStableToStable();
		else if ( testType.equals( VolatilityTestType.VOLATILE_TO_VOLATILE ) )
			testVolatileToVolatile();
		else if ( testType.equals( VolatilityTestType.STABLE_TO_VOLATILE ) )
			testStableToVolatile();
		else if ( testType.equals( VolatilityTestType.VOLATILE_TO_STABLE ) )
			testVolatileToStable();
		else if ( testType.equals( VolatilityTestType.VOLATILE_TO_STABLE_CATCH_EXCEPTION ) )
			testVolatileToStableCatchException();
		else
			throw new IllegalStateException( "Unexpected typeType: " + testType );
	}

	@PersistentMethod( volatility=Volatility.STABLE )
	private void testStableToStable()
	{
		stableMethod();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void testVolatileToVolatile()
	{
		volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	private void testStableToVolatile()
	{
		volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void testVolatileToStable()
	{
		stableMethod();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void testVolatileToStableCatchException()
	{
		try
		{
			stableMethod();
		}
		catch ( VolatilityException e )
		{
			return;
		}
		
		throw new RuntimeException( "Illegal invocation of a stable method from a volatile method." );
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	private void stableMethod()
	{
		PProcess currentProcess = pEnvironment.getPProcessManager().getCurrentProcess();
		
		if ( !currentProcess.getVolatility().equals( Volatility.STABLE ) )
			throw new RuntimeException( "Error: volatility should be STABLE but was " + currentProcess.getVolatility() + "." );
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void volatileMethod()
	{
		PProcess currentProcess = pEnvironment.getPProcessManager().getCurrentProcess();
		
		if ( !currentProcess.getVolatility().equals( Volatility.VOLATILE ) )
			throw new RuntimeException( "Error: volatility should be VOLATILE but was " + currentProcess.getVolatility() + "." );
	}
}
