package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;
import ch.shaktipat.saraswati.test.common.TestEvent;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class PersistentListeningRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private SendAndListenTestVariant testVariant;
	private PEventQueue queue;
	private Event testEvent1;
	private Event testEvent2;
	private Event testEvent3;
	
	public PersistentListeningRunnable( SendAndListenTestVariant testVariant )
	{
		this.testVariant = testVariant;
	}
	
	public void setup()
	{
		queue = PEnvironmentFactory.createLocal().getPEventQueueManager().create( "test queue", Scope.GLOBAL );
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
			if ( testVariant.equals( SendAndListenTestVariant.TEST_SEND_BEFORE_LISTEN ) )
				testSendBeforeListen();
			else if ( testVariant.equals( SendAndListenTestVariant.TEST_LISTEN_BEFORE_SEND ) )
				testListenBeforeSend();
			else if ( testVariant.equals( SendAndListenTestVariant.TEST_LISTEN_WITH_DEADLINES ) )
				testListenWithDeadlines();
			else if ( testVariant.equals( SendAndListenTestVariant.TEST_COVERAGE ) )
				testCoverage();
			else
				throw new IllegalStateException( "Unexpected type for testVariant: " + testVariant );
		}
		finally
		{
			teardown();
		}
	}
	
	private void testSendBeforeListen()
	{
		// Send an event.
		queue.send( testEvent1 );
		
		// Listen for the event.
		Event receivedEvent = queue.listen();

		// Make the result available for the unit test.
		InterprocessCommunicationHelper.setProperty( PersistentProcessPersistentEventQueueUserSpaceTest.TEST_SEND_BEFORE_LISTEN_RESULT, receivedEvent );
	}
	
	public void testListenBeforeSend()
	{
		ConcurrentTestingHelper.setupTest(
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_TEST_NAME,
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_LISTENING_CONDITION );
		
		try
		{
			testListenBeforeSend2();
		}
		finally
		{
			ConcurrentTestingHelper.clearTest();
		}
	}
	
	private void testListenBeforeSend2()
	{
		// Listen to the queue in a separate thread.
		NonPersistentListeningRunnable listeningRunnable = new NonPersistentListeningRunnable( queue );
		ThreadProxy listeningThreadProxy = new ThreadProxy( listeningRunnable );
		listeningThreadProxy.start();

		// Make sure listen occurs before send.
		ConcurrentTestingHelper.await(
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_TEST_NAME,
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_LISTENING_CONDITION );
		
		// Send an event.
		queue.send( testEvent1 );

		// Wait for listen to finish.
		listeningThreadProxy.join();
		Event receivedEvent = listeningRunnable.getReceivedEvent();
		
		// Make the result available for the unit test.
		InterprocessCommunicationHelper.setProperty( PersistentProcessPersistentEventQueueUserSpaceTest.TEST_LISTEN_BEFORE_SEND_RESULT, receivedEvent );
	}
	
	public void testListenWithDeadlines()
	{
		// Setup a deadline one second in the future and
		// use for listening.
	    Date deadline = getDeadline( 1 );
	    queue.listen( deadline );
	
		// Make the result available for the unit test.
		InterprocessCommunicationHelper.setProperty( PersistentProcessPersistentEventQueueUserSpaceTest.TEST_LISTEN_WITH_DEADLINES_RESULT, deadline );
	}

	public void testCoverage()
	{
		for ( int i = 0 ; i < 14 ; ++i )
			queue.send( testEvent1 );
		
		Date tomorrow = getDeadline( 60 * 60 * 24 );
		String filterExpression = "event.getProperty( 'testing' ).equals( '42' )";
		List< Event > recievedEvents = new ArrayList< Event >();
		
		recievedEvents.add( queue.listen() );
		recievedEvents.add( queue.listen( 1, TimeUnit.DAYS ) );
		recievedEvents.add( queue.listen( tomorrow ) );
		recievedEvents.add( queue.tryListen() );
		recievedEvents.add( queue.listen( filterExpression ) );
		recievedEvents.add( queue.listen( filterExpression, 1, TimeUnit.DAYS ) );
		recievedEvents.add( queue.listen( filterExpression, tomorrow ) );
		recievedEvents.add( queue.tryListen( filterExpression ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC, 1, TimeUnit.DAYS ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC, tomorrow ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC, filterExpression ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC, filterExpression, 1, TimeUnit.DAYS ) );
		recievedEvents.add( queue.listen( Volatility.DYNAMIC, filterExpression, tomorrow ) );
		
		InterprocessCommunicationHelper.setProperty( PersistentProcessPersistentEventQueueUserSpaceTest.TEST_COVERAGE_RESULT, recievedEvents );
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
