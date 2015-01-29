package ch.shaktipat.saraswati.internal.dynproxies;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.ExecutionProtection.EXECUTION_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.ExecutionProtection.EXECUTION_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.ASYNCHRONOUS;
import static ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine.REFERENCABLE_STATE;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.ExecutionProtection;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PProcessEvent;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PersistentProcessInvoker extends PersistentObjectInvoker
{
	private static final Method JOIN1_METHOD = ClassWrapper.getDeclaredMethod( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD );
	private static final Method JOIN2_METHOD = ClassWrapper.getDeclaredMethod( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, long.class, TimeUnit.class );
	private static final Method JOIN3_METHOD = ClassWrapper.getDeclaredMethod( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, Date.class );
	private static final Set< Method > JOIN_METHODS = new HashSet< Method >();
	
	static
	{
		JOIN_METHODS.add( JOIN1_METHOD );
		JOIN_METHODS.add( JOIN2_METHOD );
		JOIN_METHODS.add( JOIN3_METHOD );
	}
	
	public PersistentProcessInvoker( PProcessOID targetOID, MemoryModel memoryModel )
	{
		super( targetOID, memoryModel );
	}

	@Override
	protected Object invoke( PersistentObject persistentObject, Object targetObject, Method method, ProxyMethod proxyMethod, Object[] args ) throws Exception
	{
		Object retVal = null;

		// For persistent processes we can bascially ignore the targetObject
		// which should always be identical to the isolatedObject.
		assert( persistentObject == targetObject );
		
		PersistentProcess persistentProcess = (PersistentProcess)persistentObject;
		
		if ( JOIN_METHODS.contains( method ) )
			handleJoin( method, args );
		else
			retVal = handleNormalInvocation( persistentProcess, targetObject, method, proxyMethod, args );
		
		return retVal;
	}
	
	private void handleJoin( Method method, Object[] args ) throws InterruptedException
	{
		ConcurrentTestingHelper.await(
			ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_TEST_NAME, 
			ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_PUBLISHED_CONDITION );

		// Setup a temporary event queue.
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PEventQueueOID eventQueueOID = omniObjectPool.createPersistentEventQueue( "persistent process invoker queue", false, null );
		PersistentEventQueue eventQueue = PersistentEventQueueImpl.getOtherProxy( eventQueueOID );

		// Subscribe with the target process's event topic.
		PersistentProcess pProcess = PersistentProcessImpl.getOtherProxy( getTargetOID() );
		String topicfilter = "event.getProperty( '" + PProcessEvent.ENTERED_PROCESS_STATE + "' ).equals( '" + PProcessEvent.TERMINATED_STATE +
			"' ) && event.getSenderOID().toString().equals( '" + pProcess.getOID().toString() + "' )";
		UUID subscriptionOID = pProcess.subscribe( eventQueue.getOID(), topicfilter, true );
		boolean waitForJoinEvent = false;

		ConcurrentTestingHelper.signal(
			ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_TEST_NAME, 
			ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_SUBSCRIBED_CONDITION );
		
		// If the process was in the TERMINATED_STATE after registering to recieve
		// PROCESS_TERMINATED_EVENT (above); depending on timing, we may or may not
		// actually receive the event.
		if ( pProcess.isInState( PersistentProcessStateMachine.TERMINATED_STATE ) )
		{
			// Unsubscribe from topic
			boolean success = pProcess.unsubscribe( subscriptionOID );
			
			// If we were unable to unsubscribe --> the event must have already been
			// sent --> we will need to wait for the event below.
			if ( !success )
				waitForJoinEvent = true;
		}
		// If the process was not in the TERMINATED_STATE after registering to recieve
		// PROCESS_TERMINATED_EVENT --> we know for sure that we will receive the event
		// and can thus wait for the event.
		else
		{
			waitForJoinEvent = true;
		}

		if ( waitForJoinEvent )
		{
			String queuefilter = "event.getSubscriptionOID().toString().equals( '" + subscriptionOID + "' )";
			
			if ( method.equals( JOIN1_METHOD ) )
				eventQueue.listen( queuefilter );
			else if ( method.equals( JOIN2_METHOD ) )
				eventQueue.listen( queuefilter, (long)args[ 0 ], (TimeUnit)args[ 1 ] );
			else if ( method.equals( JOIN3_METHOD ) )
				eventQueue.listen( queuefilter, (Date)args[ 0 ] );
		}
		
		eventQueue.destroy();
	}
	
	private Object handleNormalInvocation( PersistentProcess persistentProcess, Object targetObject, Method method, ProxyMethod proxyMethod, Object[] args ) throws Exception
	{
		Object retVal = null;
		
		ExecutionProtection executionProtection = proxyMethod.executionStateProtection();
		Synchronicity synchronicity = proxyMethod.synchronicity();

		lock( persistentProcess, executionProtection );
		
		try
		{
			setupThreadLocals( persistentProcess, synchronicity );
			
			try
			{
				// Note that it is crucial that we invoke validateState() AFTER obtaining
				// any execution locks, since the state invariants are only then valid.
				validateState( persistentProcess, proxyMethod, method );
				
				retVal = super.invoke( persistentProcess, targetObject, method, proxyMethod, args );
			}
			finally
			{
				teardownThreadLocals( synchronicity );
			}
		}
		finally
		{
			unlock( persistentProcess, executionProtection );
		}

		return retVal;
	}
	
	private void validateState( PersistentProcess persistentProcess, ProxyMethod proxyMethod, Method actualMethod )
	{
		List< String > validProcessStates = Arrays.asList( proxyMethod.validProcessStates() );
		
		// Note that this is critical to avoid infinite recursion when
		// invoking persistentProcess.getSelfProxy().getStates() (getStates()
		// does not define any vali
		if ( validProcessStates.contains( REFERENCABLE_STATE ) )
			return;
		
		// Make sure we get the right proxy (a process coud be calling itself or another process).
		PersistentProcess proxy = null;
		
		if ( persistentProcess.getOID().equals( PersistentProcessImpl.getCurrentProcessOID() ) )
			proxy = persistentProcess.getSelfProxy();
		else
			proxy = persistentProcess.getOtherProxy();
		
		// Check the states.
		List< String > actualProcessStates = Arrays.asList( proxy.getStates() );
		
		if ( Collections.disjoint( validProcessStates, actualProcessStates ) )
		{
			String name = proxy.getName();
			String msg = "Before invoking " + actualMethod.toString() +
				" persistent process '" + name +
				"' must be in one of states [" + StringUtils.join( validProcessStates, "," ) +
				"] but was in states [" + StringUtils.join( actualProcessStates, "," ) + "].";
			throw new RuntimeException( msg );
		}
	}

	private void setupThreadLocals( PersistentProcess persistentProcess, Synchronicity synchronicity )
	{
		if ( synchronicity.equals( ASYNCHRONOUS ) )
			PersistentProcessImpl.setCurrentProcessOID( persistentProcess.getOID() );
	}
	
	private void teardownThreadLocals( Synchronicity synchronicity )
	{
		if ( synchronicity.equals( ASYNCHRONOUS ) )
			PersistentProcessImpl.setCurrentProcessOID( null );
	}

	private void lock( PersistentProcess persistentProcess, ExecutionProtection executionProtection )
	{
		if ( executionProtection.equals( EXECUTION_READ_LOCK ) )
			persistentProcess.getExecutionStateReadLock().lock();
		else if ( executionProtection.equals( EXECUTION_WRITE_LOCK ) )
			persistentProcess.getExecutionStateWriteLock().lock();
	}
	
	private void unlock( PersistentProcess persistentProcess, ExecutionProtection executionProtection )
	{
		if ( executionProtection.equals( EXECUTION_READ_LOCK ) )
			persistentProcess.getExecutionStateReadLock().unlock();
		else if ( executionProtection.equals( EXECUTION_WRITE_LOCK ) )
			persistentProcess.getExecutionStateWriteLock().unlock();
	}

	@Override
	public PProcessOID getTargetOID()
	{
		return (PProcessOID)super.getTargetOID();
	}
}
