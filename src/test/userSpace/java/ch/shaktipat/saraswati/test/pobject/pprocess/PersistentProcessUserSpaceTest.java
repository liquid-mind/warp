package ch.shaktipat.saraswati.test.pobject.pprocess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;
import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;
import ch.shaktipat.saraswati.test.common.TestEvent;
import ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen.PersistentProcessPersistentEventQueueUserSpaceTest;


public class PersistentProcessUserSpaceTest
{
	public static final String TEST_COVERAGE_LISTEN_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_COVERAGE_LISTEN_RESULT";
	public static final String TEST_COVERAGE_SLEEP_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_COVERAGE_SLEEP_RESULT";
	public static final String TEST_COVERAGE_JOIN_RESULT = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_COVERAGE_JOIN_RESULT";
	public static final String TEST_COVERAGE_JOIN_SUCCESS = PersistentProcessPersistentEventQueueUserSpaceTest.class.getName() + "TEST_COVERAGE_JOIN_SUCCESS";
	
	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();

	@SuppressWarnings( "unchecked" )
	public void testCoverageListen()
	{
		PProcessOID oid = createAndRunProcess( PersistentProcessTestVariant.TEST_COVERAGE_LISTEN );
	    List< Event > recievedEvents = (List< Event >)InterprocessCommunicationHelper.getProperty( oid, TEST_COVERAGE_LISTEN_RESULT );
	    
		for ( Event receivedEvent : recievedEvents )
			validateEvent( receivedEvent, "42", oid );
	}

	public void testCoverageSleep()
	{
		PProcessOID oid = createAndRunProcess( PersistentProcessTestVariant.TEST_COVERAGE_SLEEP );
		Date[][] earliestAndActualDeadlines = (Date[][])InterprocessCommunicationHelper.getProperty( oid, TEST_COVERAGE_SLEEP_RESULT );
	    
		for ( Date[] earliestAndActualDeadline : earliestAndActualDeadlines )
			assertTrue( earliestAndActualDeadline[ 0 ].before( earliestAndActualDeadline[ 1 ] ) );
	}

	public void testCoverageJoin()
	{
		PProcessOID oid = createAndRunProcess( PersistentProcessTestVariant.TEST_COVERAGE_JOIN );
		String result = (String)InterprocessCommunicationHelper.getProperty( oid, TEST_COVERAGE_JOIN_RESULT );
	    
		assertNotNull( result );
		assertEquals( TEST_COVERAGE_JOIN_SUCCESS, result );
	}
	
	public void testDestroyOnCompletion()
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new TestRunnable( PersistentProcessTestVariant.TEST_DESTROY_ON_COMPLETION ) );
		pProcessHandle.setDestroyOnCompletion( true );
		pProcessHandle.start();
		
		while ( pProcessHandle.exists() )
			ThreadWrapper.sleep( 100 );
	}
	
	public void testOwnership()
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create();
		
		List< POwnableObject > ownableObjects = pEnvironment.getPOwnableObjectManager().findByOwningProcess( pProcessHandle.getOID() );
		
		for ( POwnableObject ownableObject : ownableObjects )
			assertEquals( pProcessHandle, ownableObject.getOwningProcess() );
		
		pProcessHandle.destroy();
	}
	
	public void testChildParentRelationship()
	{
		PProcessHandle parent = pEnvironment.getPProcessManager().create( new TestRunnable( PersistentProcessTestVariant.TEST_CHILD_PARENT_RELATIONSHIOP ) );
		parent.start();
		parent.join();
		PProcessHandle[] children = parent.getChildren();
		
		for ( PProcessHandle child : children )
		{
			assertEquals( parent, child.getParent() );
			child.destroy();
		}
		
		parent.destroy();
	}
	
	private PProcessOID createAndRunProcess( PersistentProcessTestVariant testVariant )
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new TestRunnable( testVariant ) );
		pProcessHandle.start();
		pProcessHandle.join();
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
}
