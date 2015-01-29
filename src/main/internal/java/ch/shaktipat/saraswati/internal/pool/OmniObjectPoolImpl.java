package ch.shaktipat.saraswati.internal.pool;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopic;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopicImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectVisitor;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public class OmniObjectPoolImpl extends PersistentObjectPoolImpl implements OmniObjectPool
{
	@Override
	public void returnPersistentObject( PersistentObject persistentObject, boolean exclusiveAccess )
	{
		super.returnPersistentObject( persistentObject, exclusiveAccess );
		enforceDestroyOnCompletion( persistentObject );
	}
	
	private void enforceDestroyOnCompletion( PersistentObject persistentObject )
	{
		if ( persistentObject instanceof PersistentProcess )
		{
			PersistentProcess pProcess = (PersistentProcess)persistentObject;
			
			// TODO I think that there is a potential race condition here due
			// to the current thread relying on pProcess.getLockHoldCount() to
			// remain zero for the duration of destroyPersistentObject().
			// An alternative approach would be the spawn a destruction thread
			// that waits until it receives the referenceWriteLock. Care must
			// be taken to ensure that only one such thread is ever spawned.
			// Another open question is how to solve signalling the transition
			// to the DESTROYED state. I don't think that the current solution
			// is great, but it may not be worth improving at this point, since
			// other matters seem more pressing, and this solution (usually, it
			// seems) works.
			if ( pProcess.getDestroyOnCompletion() &&
					pProcess.isInState( PersistentProcessStateMachine.TERMINATED_STATE ) &&
					pProcess.isReferencableInstance() &&
					pProcess.getLockHoldCount() == 0 )
				destroyPersistentObject( pProcess.getOID() );
		}
	}

	@Override
	public PProcessOID createPersistentProcess( Runnable runnable, Callable< ? > callable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion )
	{
		PersistentProcess pProcess = new PersistentProcessImpl( runnable, callable, name, initialVolatility, owner, autoCheckpointing, destroyOnCompletion );
		
		// Put the process in the pool. Note: from this point on the process
		// is concurrently accessible and any any access must therefore be
		// protected.
		addPersistentObject( pProcess );
		
		return pProcess.getOID();
	}
	
	@Override
	public PEventQueueOID createPersistentEventQueue( String name, boolean isPersistent, PProcessOID owningProcessOID )
	{
		PersistentEventQueue pEventQueue = new PersistentEventQueueImpl( name, isPersistent, owningProcessOID );
		addPersistentObject( pEventQueue );
		
		return pEventQueue.getOID();
	}
	
	@Override
	public PEventTopicOID createPersistentEventTopic( String name, boolean isPersistent, PProcessOID owningProcessOID )
	{
		PersistentEventTopic pEventTopic = new PersistentEventTopicImpl( name, isPersistent, owningProcessOID );
		addPersistentObject( pEventTopic );
		
		return pEventTopic.getOID();
	}

	@Override
	public PScheduledEventOID createScheduledEvent( String name, Event event, TimeSpecification timeSpecification, PEventQueueOID eventQueueOID )
	{
		PersistentScheduledEvent pScheduledEvent = new PersistentScheduledEventImpl( name, event, timeSpecification, eventQueueOID );
		addPersistentObject( pScheduledEvent );
		
		return pScheduledEvent.getOID();
	}

	@Override
	public PSharedObjectOID createPersistentSharedObject( String name, Object shareableObject, PProcessOID owningProcessOID )
	{
		PersistentSharedObject pSharedObject = new PersistentSharedObjectImpl( name, owningProcessOID, shareableObject );
		addPersistentObject( pSharedObject );
		
		return pSharedObject.getOID();
	}
	
	@Override
	public List< PProcessOID > findByPersistentProcessState( String state )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List< PProcessOID > findByLastStableDate( Date lastStableDateLowerBoundary, Date lastStableDateUpperBoundary )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List< PProcessOID > findByVolatileObjectState( String state )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List< PScheduledEventOID > findByEarliestEvent()
	{
		// First, fetch all scheduled events.
		final List< PScheduledEventOID > oids = new ArrayList< PScheduledEventOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				oids.add( (PScheduledEventOID)persistentObject.getOID() );
				return false;
			}
		}, PersistentScheduledEventImpl.class );
		
		// Then, identify the earliest one.
		Date earliestDate = null;
		PScheduledEventOID earliestOID = null;
		
		for ( PScheduledEventOID oid : oids )
		{
			PersistentScheduledEvent persistentScheduledEvent = (PersistentScheduledEvent)borrowPersistentObject( oid, false );
			
			try
			{
				Date deadline = persistentScheduledEvent.getDeadline();
				
				if ( earliestDate == null || deadline.before( earliestDate ) )
				{
					earliestDate = deadline;
					earliestOID = oid;
				}
			}
			finally
			{
				returnPersistentObject( persistentScheduledEvent, false );
			}
		}
		
		List< PScheduledEventOID > oidsEarliest = new ArrayList< PScheduledEventOID >();
		oidsEarliest.add( earliestOID );
		
		return oidsEarliest;
	}
}
