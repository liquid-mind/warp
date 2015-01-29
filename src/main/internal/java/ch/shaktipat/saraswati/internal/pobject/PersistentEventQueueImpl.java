package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import ch.shaktipat.exwrapper.java.util.concurrent.locks.ConditionWrapper;
import ch.shaktipat.saraswati.common.DeadlineSpecification;
import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.common.TimeAndUnitSpecification;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentEventQueueInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectListHelper;
import ch.shaktipat.saraswati.internal.pobject.aux.event.EventFilter;
import ch.shaktipat.saraswati.internal.pobject.aux.event.EventListener;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.pool.PersistentObjectNotFoundExeception;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public class PersistentEventQueueImpl extends PersistentOwnableObjectImpl implements PersistentEventQueue, PEventQueue
{
	private static final long serialVersionUID = 1L;
	
	private Queue< Event > eventQueue;
	private EventListener eventListener;
	private transient Condition eventArrivedCondition;
	private boolean waitingForEvent = false;

	public PersistentEventQueueImpl( String name, boolean isPersistent, PProcessOID owningProcessOID )
	{
		// If this object is persistent then it is also swappable; otherwise not.
		super( name, isPersistent, isPersistent, owningProcessOID );
		eventQueue = new LinkedList<>();
		eventArrivedCondition = getStateWriteLock().newCondition();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void send( Event event )
	{
		boolean addToQueue = false;
		
		event.setSenderOID( PersistentProcessImpl.getCurrentProcessOID() );
		event.setPEventQueueOID( getOID() );
		
		if ( eventListener != null )
			addToQueue = dispatchEventToPersistentProcess( event );
		else if ( waitingForEvent )
			dispatchEventToJavaThread( event );
		else
			addToQueue = true;
		
		if ( addToQueue )
			eventQueue.add( event );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void registerListener( PProcessOID targetProcessOID, String filterExpression, TimeSpecification timeSpecification )
	{
		// Throw an exception an eventListener is already registered.
		if ( eventListener != null )
			throw new RuntimeException( "Listener already set: targetProcessOID=" + targetProcessOID + ", queueOID=" + getOID() );
		
		// If a timeout was specified --> schedule timeout event.
		PScheduledEventOID scheduledTimeoutEventOID = null;

		if ( timeSpecification != null)
			scheduledTimeoutEventOID = PEngine.getPEngine().getPersistentScheduler().scheduleEvent( getOID(), timeSpecification );

		// Register the listener.
		EventFilter eventFilter = new EventFilter( filterExpression );
		eventListener = new EventListener( targetProcessOID, scheduledTimeoutEventOID, eventFilter );
		
		// Try to immediately dispatch an event to the listener.
		Iterator< Event > iter = eventQueue.iterator();
		while ( iter.hasNext() )
		{
			if ( dispatchEventToPersistentProcess( iter.next() ) )
			{
				iter.remove();
				break;
			}
		}
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public boolean unregisterListener()
	{
		boolean unregistered = false;
		
		if ( eventListener != null )
		{
			// Unschedule the timeout event.
			PScheduledEventOID timeoutEventOID = eventListener.getScheduledTimeoutEventOID();
			if ( timeoutEventOID != null )
				PEngine.getPEngine().getPersistentScheduler().unscheduleEvent( timeoutEventOID );

			// "Unregister". 
			eventListener = null;
			unregistered = true;
		}
		
		return unregistered;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public EventListener getListener()
	{
		return eventListener;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void handleTimeoutEvent( PScheduledEventOID pScheduledEventOID )
	{
		// If there is no event listener registerd or if the
		// scheduled timeout OID doesn't match --> drop event.
		if ( eventListener == null || !eventListener.getScheduledTimeoutEventOID().equals( pScheduledEventOID ) )
			return;
		
		// Otherwise, send the timeout event to the process and
		// unregister the listener.
		PersistentScheduledEvent pScheduledEvent = PersistentScheduledEventImpl.getOtherProxy( pScheduledEventOID );
		Event event = pScheduledEvent.getEvent();
		event.setPEventQueueOID( getOID() );
		forwardEventToProcess( eventListener.getTargetProcessOID(), event );
	}
	
	private void forwardEventToProcess( PProcessOID pProcessOID, Event event )
	{
		PersistentProcess pProcess = null;
		
		try
		{
			pProcess = PersistentProcessImpl.getOtherProxy( eventListener.getTargetProcessOID() );
		}
		catch ( PersistentObjectNotFoundExeception e )
		{
			// Ignore exception.
		}
		
		if ( pProcess != null )
			pProcess.handleEvent( event );
	}
	
	private void dispatchEventToJavaThread( Event event )
	{
		if ( waitingForEvent )
		{
			waitingForEvent = false;
			eventArrivedCondition.signal();
		}
		
		eventQueue.add( event );
	}
	
	private boolean dispatchEventToPersistentProcess( Event event )
	{
		boolean eventDispatched = false;
		
		// If an event listener is registered and the event filter matches -->
		// alert the listener.
		if ( eventListener != null && eventListener.getEventFilter().matches( event ) )
		{
			// Dispatch the event to the appropriate process.
			forwardEventToProcess( eventListener.getTargetProcessOID(), event );
			eventDispatched = true;
		}

		return eventDispatched;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen( String filterExpression )
	{
		return listenFactored( filterExpression, null );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen( String filterExpression, long time, TimeUnit timeUnit )
	{
		return listenFactored( filterExpression, new TimeAndUnitSpecification( time, timeUnit ) );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen( String filterExpression, Date deadline )
	{
		return listenFactored( filterExpression, new DeadlineSpecification( deadline ) );
	}
	
	private Event listenFactored( String filterExpression, TimeSpecification timeSpecification )
	{
		ConcurrentTestingHelper.signal(
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_TEST_NAME,
			ConcurrentTestingHelper.LISTEN_BEFORE_SEND_LISTENING_CONDITION );

		Event firstMatchingEvent = null;
		Date deadline = null;
		boolean timeoutOccurred = false;
		
		// We calculate the deadline once here to ensure it
		// stays constant (rather than being recalculated, e.g.,
		// when the thread is "spuriously" woken up.
		if ( timeSpecification != null )
			deadline = timeSpecification.calcDeadline();

		while ( ( firstMatchingEvent = getFirstMatchingEvent( filterExpression ) ) == null && !timeoutOccurred )
		{
			waitingForEvent = true;
			
			if ( deadline == null )
			{
				ConditionWrapper.await( eventArrivedCondition );
			}
			else
			{
				ConditionWrapper.await( eventArrivedCondition, deadline );
				Date currentTime = new Date();
				
				if ( currentTime.after( deadline ) )
					timeoutOccurred = true;	
			}
		}
		
		return firstMatchingEvent;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event tryListen( String filterExpression )
	{
		Event event = getFirstMatchingEvent( filterExpression );
		
		return event;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public List< Class< ? > > getQueuedEventTypes()
	{
		List< Class< ? > > eventTypes = new ArrayList< Class< ? > >();
		
		for ( Event event : eventQueue )
			eventTypes.add( event.getClass() );
		
		return eventTypes;
	}
	
	private void writeObject( java.io.ObjectOutputStream stream ) throws IOException
	{
		eventArrivedCondition = null;
		stream.defaultWriteObject();
	}
	
	private void readObject( java.io.ObjectInputStream stream ) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		eventArrivedCondition = getStateWriteLock().newCondition();
	}
	
	// Note that the listen() methods that specify a volatility cannot be invoked
	// from a Java thread context.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility, String filterExpression ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility, String filterExpression, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility, String filterExpression, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public Event listen( Volatility volatility, Date deadline ) { throw new UnsupportedOperationException(); }
	
	private Event getFirstMatchingEvent( String eventFilterAsString )
	{
		EventFilter eventFilter = new EventFilter( eventFilterAsString );
		Iterator< Event > iter = eventQueue.iterator();
		Event matchingEvent = null;
		
		while ( iter.hasNext() )
		{
			Event nextEvent = iter.next();
			if ( eventFilter.matches( nextEvent ) )
			{
				matchingEvent = nextEvent;
				iter.remove();
				break;
			}
		}
		
		return matchingEvent;
	}

	@Override
	@ProxyMethod( synchronicity = Synchronicity.SYNCHRONOUS )
	public String getDefaultName()
	{
		return PersistentEventQueue.class.getSimpleName() + "-" + getOID();
	}

	////////////////////////////
	// DELEGATING LISTEN METHODS
	////////////////////////////

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen()
	{
		return listen( (String)null );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen( long time, TimeUnit timeUnit )
	{
		return listen( (String)null, time, timeUnit );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event listen( Date deadline )
	{
		return listen( (String)null, deadline );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public Event tryListen()
	{
		return tryListen( (String)null );
	}

	/////////////////
	// CREATE METHODS
	/////////////////

	public static PEventQueue create()
	{
		return create( null );
	}
	
	public static PEventQueue create( Scope scope )
	{
		return create( null, Scope.GLOBAL );
	}
	
	public static PEventQueue create( String name, Scope scope )
	{
		boolean isPersistent = ( PersistentProcessImpl.getCurrentProcessOID() != null );
		PEventQueue pEventQueue = create( name, isPersistent, PersistentOwnableObjectImpl.deriveOwningProcessOIDFromScope( scope ) );
		
		return pEventQueue;
	}
	
	public static PEventQueue create( String name, boolean isPersistent, PProcessOID owningProcessOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PEventQueueOID pEventQueueOID = omniObjectPool.createPersistentEventQueue( name, isPersistent, owningProcessOID );
		PEventQueue pEventQueue = createPEventQueueProxy( pEventQueueOID );
		
		return pEventQueue;
	}
	
	///////////////
	// FIND METHODS
	///////////////
	
	public static PEventQueue findByOID( PEventQueueOID oid )
	{
		return createPEventQueueProxy( oid );
	}
	
	@SuppressWarnings( "unchecked" )
	public static List< PEventQueue > findByName( String name )
	{
		List< PEventQueueOID > oids = (List< PEventQueueOID >)(Object)findOIDsByName( name, PersistentEventQueueImpl.class );
		List< PEventQueue > pEventQueues = PersistentObjectListHelper.createExternalProxyList( PEventQueue.class, oids );
		
		return pEventQueues;
	}

	////////////////
	// PROXY METHODS
	////////////////
	
	@Override
	public PersistentEventQueue getSelfProxy()
	{
		return (PersistentEventQueue)super.getSelfProxy();
	}

	@Override
	public PersistentEventQueue getOtherProxy()
	{
		return (PersistentEventQueue)super.getOtherProxy();
	}

	public static PersistentEventQueue getOtherProxy( PEventQueueOID pEventQueueOID )
	{
		return (PersistentEventQueue)getProxy( ProxyType.INTERNAL_OTHER, pEventQueueOID );
	}
	
	@Override
	protected PersistentEventQueue createSelfProxy()
	{
		return createProxy( ProxyType.INTERNAL_SELF, getOID() );
	}

	@Override
	protected PersistentEventQueue createOtherProxy()
	{
		return createProxy( ProxyType.INTERNAL_OTHER, getOID() );
	}
	
	public static PEventQueue createPEventQueueProxy( PEventQueueOID pEventQueueOID )
	{
		return createProxy( ProxyType.EXTERNAL_OTHER, pEventQueueOID );
	}
	
	private static < T > T createProxy( ProxyType proxyType, PEventQueueOID pEventQueueOID )
	{
		return createProxy( proxyType, pEventQueueOID, PersistentEventQueue.class, PEventQueue.class, PEventQueue.class, new PersistentEventQueueInvocationHandler() );
	}

	@Override
	public PEventQueueOID getOID()
	{
		return (PEventQueueOID)super.getOID();
	}
}
