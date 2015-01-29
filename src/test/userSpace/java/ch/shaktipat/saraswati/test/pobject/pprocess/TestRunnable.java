package ch.shaktipat.saraswati.test.pobject.pprocess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;
import ch.shaktipat.saraswati.test.common.TestEvent;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class TestRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();
	
	private PersistentProcessTestVariant testVariant;
	private PEventQueue queue;
	private Event testEvent1;
	private Event testEvent2;
	private Event testEvent3;
	
	public TestRunnable( PersistentProcessTestVariant testVariant )
	{
		this.testVariant = testVariant;
	}
	
	public void setup()
	{
		queue = pEnvironment.getPEventQueueManager().create( "test queue", Scope.GLOBAL );
		testEvent1 = new TestEvent();
		testEvent1.setProperty( "testing", "42" );
		testEvent2 = new TestEvent();
		testEvent2.setProperty( "testing", "43" );
		testEvent3 = new TestEvent();
		testEvent3.setProperty( "testing", "44" );
	}
	
	public void teardown()
	{
		if ( queue != null )
			queue.destroy();
	}
	
	@Override
	public void run()
	{
		setup();
		
		try
		{
			if ( testVariant == null )
				return;
			else if ( testVariant.equals( PersistentProcessTestVariant.TEST_COVERAGE_LISTEN ) )
				testCoverageListen();
			else if ( testVariant.equals( PersistentProcessTestVariant.TEST_COVERAGE_SLEEP ) )
				testCoverageSleep();
			else if ( testVariant.equals( PersistentProcessTestVariant.TEST_COVERAGE_JOIN ) )
				testCoverageJoin();
			else if ( testVariant.equals( PersistentProcessTestVariant.TEST_DESTROY_ON_COMPLETION ) )
				testDestroyOnCompletion();
			else if ( testVariant.equals( PersistentProcessTestVariant.TEST_CHILD_PARENT_RELATIONSHIOP ) )
				testChildParentRelationship();
			else
				throw new IllegalStateException( "Unexpected type for testVariant: " + testVariant );
		}
		finally
		{
			teardown();
		}
	}

	public void testCoverageListen()
	{
		PProcess pProcess = PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess();
		
		for ( int i = 0 ; i < 14 ; ++i )
			pProcess.send( testEvent1 );
		
		// Make sure we don't timeout (even during testing).
		Date tomorrow = getDeadline( 60 * 60 * 24 );
		String filterExpression = "event.getProperty( 'testing' ).equals( '42' )";
		List< Event > receivedEvents = new ArrayList< Event >();
		
		receivedEvents.add( pProcess.listen() );
		receivedEvents.add( pProcess.listen( 1, TimeUnit.DAYS ) );
		receivedEvents.add( pProcess.listen( tomorrow ) );
		receivedEvents.add( pProcess.tryListen() );
		receivedEvents.add( pProcess.listen( filterExpression ) );
		receivedEvents.add( pProcess.listen( filterExpression, 1, TimeUnit.DAYS ) );
		receivedEvents.add( pProcess.listen( filterExpression, tomorrow ) );
		receivedEvents.add( pProcess.tryListen( filterExpression ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC, 1, TimeUnit.DAYS ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC, tomorrow ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC, filterExpression ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC, filterExpression, 1, TimeUnit.DAYS ) );
		receivedEvents.add( pProcess.listen( Volatility.DYNAMIC, filterExpression, tomorrow ) );
		
		InterprocessCommunicationHelper.setProperty( PersistentProcessUserSpaceTest.TEST_COVERAGE_LISTEN_RESULT, receivedEvents );
	}	

	public void testCoverageSleep()
	{
		PProcess pProcess = pEnvironment.getPProcessManager().getCurrentProcess();

		Date[][] earliestAndActualDeadlines = new Date[ 4 ][ 2 ];
		
		earliestAndActualDeadlines[ 0 ][ 0 ] = new Date();
		pProcess.sleep( 1, TimeUnit.SECONDS );
		earliestAndActualDeadlines[ 0 ][ 1 ] = new Date();
		
		earliestAndActualDeadlines[ 1 ][ 0 ] = new Date();
		pProcess.sleep( getDeadline( 1 ) );
		earliestAndActualDeadlines[ 1 ][ 1 ] = new Date();
		
		earliestAndActualDeadlines[ 2 ][ 0 ] = new Date();
		pProcess.sleep( Volatility.DYNAMIC, 1, TimeUnit.SECONDS );
		earliestAndActualDeadlines[ 2 ][ 1 ] = new Date();
		
		earliestAndActualDeadlines[ 3 ][ 0 ] = new Date();
		pProcess.sleep( Volatility.DYNAMIC, getDeadline( 1 ) );
		earliestAndActualDeadlines[ 3 ][ 1 ] = new Date();
		
		InterprocessCommunicationHelper.setProperty( PersistentProcessUserSpaceTest.TEST_COVERAGE_SLEEP_RESULT, earliestAndActualDeadlines );
	}

	public void testCoverageJoin()
	{
		for ( int i = 0 ; i < 6 ; ++i )
			testCoverageJoin2( i );
		
		InterprocessCommunicationHelper.setProperty(
			PersistentProcessUserSpaceTest.TEST_COVERAGE_JOIN_RESULT,
			PersistentProcessUserSpaceTest.TEST_COVERAGE_JOIN_SUCCESS );
	}
	
	public void testDestroyOnCompletion()
	{
		// do nothing.
	}
	
	public void testChildParentRelationship()
	{
		PProcessManager pProcessManager = pEnvironment.getPProcessManager();
		pProcessManager.create();
		pProcessManager.create();
		pProcessManager.create();
	}
	
	private void testCoverageJoin2( int variant )
	{
		Date tomorrow = getDeadline( 60 * 60 * 24 );

		// Create a process that simply returns (testVariant == null)
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new TestRunnable( null ) );
		pProcessHandle.start();

		if ( variant == 0 )
			pProcessHandle.join();
		else if ( variant == 1 )
			pProcessHandle.join( 1, TimeUnit.DAYS );
		else if ( variant == 2 )
			pProcessHandle.join( tomorrow );
		else if ( variant == 3 )
			pProcessHandle.join( Volatility.DYNAMIC );
		else if ( variant == 4 )
			pProcessHandle.join( Volatility.DYNAMIC, 1, TimeUnit.DAYS );
		else if ( variant == 5 )
			pProcessHandle.join( Volatility.DYNAMIC, tomorrow );
		
		pProcessHandle.destroy();
	}
	
	private Date getDeadline( int secondsInFuture )
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime( new Date() );
	    cal.add( Calendar.SECOND, secondsInFuture );
	    Date deadline = cal.getTime();
	    
	    return deadline;
	}
}
