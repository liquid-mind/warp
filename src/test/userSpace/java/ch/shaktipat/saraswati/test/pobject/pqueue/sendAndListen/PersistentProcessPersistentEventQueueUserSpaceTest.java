package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;
import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;
import ch.shaktipat.saraswati.test.common.TestEvent;

public class PersistentProcessPersistentEventQueueUserSpaceTest
{
	public static final String TEST_SEND_BEFORE_LISTEN_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_SEND_BEFORE_LISTEN_RESULT";
	public static final String TEST_LISTEN_BEFORE_SEND_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_LISTEN_BEFORE_SEND_RESULT";
	public static final String TEST_LISTEN_WITH_DEADLINES_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_LISTEN_WITH_DEADLINES_RESULT";
	public static final String TEST_COVERAGE_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_COVERAGE_RESULT";
	
	public void testSendBeforeListen()
	{
		PProcessOID oid = createAndRunProcess( SendAndListenTestVariant.TEST_SEND_BEFORE_LISTEN );
		Event receivedEvent = (Event)InterprocessCommunicationHelper.getProperty( oid, TEST_SEND_BEFORE_LISTEN_RESULT );
		validateEvent( receivedEvent, "42", oid );
	}
	
	public void testListenBeforeSend()
	{
		PProcessOID oid = createAndRunProcess( SendAndListenTestVariant.TEST_LISTEN_BEFORE_SEND );
		Event receivedEvent = (Event)InterprocessCommunicationHelper.getProperty( oid, TEST_LISTEN_BEFORE_SEND_RESULT );
		validateEvent( receivedEvent, "42", oid );
	}
	
	public void testListenWithDeadlines()
	{
		PProcessOID oid = createAndRunProcess( SendAndListenTestVariant.TEST_LISTEN_WITH_DEADLINES );
	    Date currentTime = new Date();
		Date deadline = (Date)InterprocessCommunicationHelper.getProperty( oid, TEST_LISTEN_WITH_DEADLINES_RESULT );
	    assertTrue( currentTime.after( deadline ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public void testCoverage()
	{
		PProcessOID oid = createAndRunProcess( SendAndListenTestVariant.TEST_COVERAGE );
	    List< Event > recievedEvents = (List< Event >)InterprocessCommunicationHelper.getProperty( oid, TEST_COVERAGE_RESULT );
	    
		for ( Event receivedEvent : recievedEvents )
			validateEvent( receivedEvent, "42", oid );
	}
	
	private PProcessOID createAndRunProcess( SendAndListenTestVariant testVariant )
	{
		PProcessHandle pProcessHandle = PEnvironmentFactory.createLocal().getPProcessManager().create( new PersistentListeningRunnable( testVariant ) );
		pProcessHandle.start();
		joinProcess( pProcessHandle );
		pProcessHandle.destroy();
		
		return pProcessHandle.getOID();
	}
	
	private void validateEvent( Event event, String propValue, PProcessOID pProcessOID )
	{
		assertNotNull( event );
		assertTrue( event instanceof TestEvent );
		assertEquals( propValue, event.getProperty( "testing" ) );
		assertNull( event.getSubscriptionOID() );
		assertEquals( pProcessOID, event.getSenderOID() );
	}
	
	private void joinProcess( PProcessHandle pProcessHandle )
	{
		// Note that we cannot use join() since that method
		// itself is dependant on listen(); instead, we just
		// pool for the TERMINATED_STATE.
		while ( !pProcessHandle.isInState( PersistentProcessStateMachine.TERMINATED_STATE ) )
			ThreadWrapper.sleep( 100 );
	}
}
