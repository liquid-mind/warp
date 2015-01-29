package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.test.common.TestEvent;
import ch.shaktipat.saraswati.volatility.Volatility;

public class JavaThreadPersistentEventQueueUserSpaceTest
{
	private PEventQueue queue;
	private Event testEvent1;
	private Event testEvent2;
	private Event testEvent3;
	
	// TODO A number of tests use the same setup() and teardown()
	// as well as redefining getDeadline() and validateEvent();
	// refactor into common class.
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
	
	public void testSendBeforeListen() throws InterruptedException
	{
		// Send an event.
		queue.send( testEvent1 );
		
		// Listen for the event.
		Event receivedEvent = queue.listen();
		
		// Validate test.
		validateEvent( receivedEvent, "42" );
	}
	
	public void testListenBeforeSend() throws InterruptedException
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
	
	private void testListenBeforeSend2() throws InterruptedException
	{
		final Map< String, Event > innerClassCommMap = new HashMap< String, Event >();
		
		// Listen to the queue in a separate thread.
		Thread listeningThread = new Thread( new Runnable() {
			@Override
			public void run()
			{
				Event receivedEvent = queue.listen();
				innerClassCommMap.put( "receivedEvent", receivedEvent );
			}
		} );
		listeningThread.start();

		// Make sure listen occurs before send.
		ConcurrentTestingHelper.await(
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_TEST_NAME,
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_LISTENING_CONDITION );
		
		// Send an event.
		queue.send( testEvent1 );

		// Wait for listen to finish.
		listeningThread.join();
		Event receivedEvent = innerClassCommMap.get( "receivedEvent" );
		
		// Validate test.
		validateEvent( receivedEvent, "42" );
	}
	
	public void testTryListen() throws InterruptedException
	{
		// Send an event.
		queue.send( testEvent1 );
		
		// Listen for the event.
		Event receivedEvent = queue.tryListen();
		
		// Validate test.
		validateEvent( receivedEvent, "42" );
	}

	public void testFilter() throws InterruptedException
	{
		// Send an event.
		queue.send( testEvent1 );
		
		// Listen for the event.
		Event nullEvent1 = queue.tryListen( "event.getProperty( 'testing' ).equals( '43' )" );
		Event nullEvent2 = queue.tryListen( "invalid expression" );
		Event receivedEvent = queue.tryListen( "event.getProperty( 'testing' ).equals( '42' )" );
		
		// Validate test.
		assertNull( nullEvent1 );
		assertNull( nullEvent2 );
		validateEvent( receivedEvent, "42" );
	}

	public void testOrder() throws InterruptedException
	{
		// Send two events.
		queue.send( testEvent1 );
		queue.send( testEvent2 );
		
		// Listen for the event.
		Event receivedEvent = queue.listen();
		
		// Validate test (make sure queue is behaving fifo).
		validateEvent( receivedEvent, "42" );
	}

	public void testFilterAndOrder() throws InterruptedException
	{
		// Send an event.
		queue.send( testEvent1 );
		queue.send( testEvent2 );
		queue.send( testEvent3 );
		
		// Listen for the event.
		Event receivedEvent1 = queue.listen( "event.getProperty( 'testing' ).equals( '43' )" );
		Event receivedEvent2 = queue.listen();
		Event receivedEvent3 = queue.listen();
		
		// Validate test (make sure queue is behaving fifo)
		validateEvent( receivedEvent1, "43" );
		validateEvent( receivedEvent2, "42" );
		validateEvent( receivedEvent3, "44" );
	}
	
	public void testListenWithDeadlines() throws InterruptedException
	{
		// Setup a deadline one second in the future and
		// use for listening.
	    Date deadline = getDeadline( 1 );
	    Event receivedEvent = queue.listen( deadline );
	    Date currentTime = new Date();
	
	    // Make sure the current time is after the deadline
	    // and validate the event.
	    assertTrue( currentTime.after( deadline ) );
	    assertNull( receivedEvent );
	}
	
	public void testListenWithTimeAndTimeunit()
	{
		// In this case, the deadline is simply used to measure the
		// amount of time that has elapsed, whereas the actual timeout
		// is specified through the time/time unit API.
	    Date deadline = getDeadline( 1 );
	    Event receivedEvent = queue.listen( 1, TimeUnit.SECONDS );
	    Date currentTime = new Date();
	
	    // Make sure the current time is after the deadline
	    // and validate the event.
	    assertTrue( currentTime.after( deadline ) );
	    assertNull( receivedEvent );
	}
	
	public void testCoverage()
	{
		for ( int i = 0 ; i < 8 ; ++i )
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
		
		for ( Event receivedEvent : recievedEvents )
			validateEvent( receivedEvent, "42" );
		
		for ( int i = 0 ; i < 6 ; ++i )
		{
			Throwable result = testCoverageWithExceptionHandling( i, tomorrow, filterExpression );
			assertEquals( UnsupportedOperationException.class, result.getClass() );
		}
	}
	
	private Throwable testCoverageWithExceptionHandling( int variant, Date now, String filterExpression )
	{
		Throwable result = null;
		
		try
		{
			if ( variant == 0 )
				queue.listen( Volatility.DYNAMIC );
			
			if ( variant == 1 )
				queue.listen( Volatility.DYNAMIC, 1, TimeUnit.MILLISECONDS );
			
			if ( variant == 2 )
				queue.listen( Volatility.DYNAMIC, now );
			
			if ( variant == 3 )
				queue.listen( Volatility.DYNAMIC, filterExpression );
			
			if ( variant == 4 )
				queue.listen( Volatility.DYNAMIC, filterExpression, 1, TimeUnit.MILLISECONDS );
			
			if ( variant == 5 )
				queue.listen( Volatility.DYNAMIC, filterExpression, now );
		}
		catch ( Throwable t )
		{
			result = t;
		}
		
		return result;
	}
	
	private Date getDeadline( int secondsInFuture )
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime( new Date() );
	    cal.add( Calendar.SECOND, secondsInFuture );
	    Date deadline = cal.getTime();
	    
	    return deadline;
	}
	
	private void validateEvent( Event event, String propValue )
	{
		assertNotNull( event );
		assertTrue( event instanceof TestEvent );
		assertEquals( propValue, event.getProperty( "testing" ) );
		assertNull( event.getSubscriptionOID() );
		assertNull( event.getSenderOID() );
	}
}
