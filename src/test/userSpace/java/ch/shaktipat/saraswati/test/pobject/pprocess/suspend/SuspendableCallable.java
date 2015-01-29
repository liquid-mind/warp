package ch.shaktipat.saraswati.test.pobject.pprocess.suspend;

import java.io.Serializable;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;

@PersistentClass
public class SuspendableCallable implements Callable< String >, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private SuspendTestVariant testVariant;
	
	public SuspendableCallable( SuspendTestVariant testVariant )
	{
		super();
		this.testVariant = testVariant;
	}

	@Override
	public String call() throws Exception
	{
		String retVal = null;
		
		if ( testVariant.equals( SuspendTestVariant.TEST_INFINITE_LOOP ) )
			testInfiniteLoop();
		else if ( testVariant.equals( SuspendTestVariant.TEST_LISTEN ) )
			retVal = testListen();
		else
			throw new IllegalStateException( "Unexpected type for testVariant: " + testVariant );
		
		return retVal;
	}
	
	private void testInfiniteLoop()
	{
		while ( true )
			testMethod();
	}
	
	private void testMethod()
	{}
	
	private String testListen()
	{
		PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess().listen();
		
		return "success";
	}
}
