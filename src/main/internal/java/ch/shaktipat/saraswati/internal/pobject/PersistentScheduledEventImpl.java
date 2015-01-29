package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.util.Date;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentScheduledEventInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PScheduledEvent;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class PersistentScheduledEventImpl extends PersistentOwnableObjectImpl implements PersistentScheduledEvent, PScheduledEvent
{
	private static final long serialVersionUID = 1L;
	
	private Event event;
	private Date deadline;
	private PEventQueueOID eventQueueOID;

	public PersistentScheduledEventImpl( String name, Event event, TimeSpecification timeSpecification, PEventQueueOID eventQueueOID )
	{
		super( name, true, false, null );
		this.event = event;
		deadline = timeSpecification.calcDeadline();
		this.eventQueueOID = eventQueueOID;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Date getDeadline()
	{
		return deadline;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Event getEvent()
	{
		return event;
	}

	@Override
	public PEventQueue getEventListener()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PEventQueueOID getEventQueueOID()
	{
		return eventQueueOID;
	}

	@Override
	@ProxyMethod( synchronicity = Synchronicity.SYNCHRONOUS )
	public String getDefaultName()
	{
		return PersistentScheduledEvent.class.getSimpleName() + "-" + getOID();
	}

	/////////////////
	// CREATE METHODS
	/////////////////
	
	public static PScheduledEvent create( String name, Event event, TimeSpecification timeSpecification, PEventQueueOID eventQueueOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PScheduledEventOID pScheduledEventOID = omniObjectPool.createScheduledEvent( name, event, timeSpecification, eventQueueOID );
		PScheduledEvent pScheduledEvent = createPScheduledEventProxy( pScheduledEventOID );
		
		return pScheduledEvent;
	}
	
	////////////////
	// PROXY METHODS
	////////////////
	
	@Override
	public PersistentScheduledEvent getSelfProxy()
	{
		return (PersistentScheduledEvent)super.getSelfProxy();
	}

	@Override
	public PersistentScheduledEvent getOtherProxy()
	{
		return (PersistentScheduledEvent)super.getOtherProxy();
	}

	public static PersistentScheduledEvent getOtherProxy( PScheduledEventOID pScheduledEventOID )
	{
		return (PersistentScheduledEvent)getProxy( ProxyType.INTERNAL_OTHER, pScheduledEventOID );
	}
	
	@Override
	protected PersistentScheduledEvent createSelfProxy()
	{
		return createProxy( ProxyType.INTERNAL_SELF, getOID() );
	}

	@Override
	protected PersistentScheduledEvent createOtherProxy()
	{
		return createProxy( ProxyType.INTERNAL_OTHER, getOID() );
	}
	
	public static PScheduledEvent createPScheduledEventProxy( PScheduledEventOID pScheduledEventOID )
	{
		return createProxy( ProxyType.EXTERNAL_OTHER, pScheduledEventOID );
	}
	
	private static < T > T createProxy( ProxyType proxyType, PScheduledEventOID pScheduledEventOID )
	{
		return createProxy( proxyType, pScheduledEventOID, PersistentScheduledEvent.class, PScheduledEvent.class, PScheduledEvent.class, new PersistentScheduledEventInvocationHandler() );
	}

	@Override
	public PScheduledEventOID getOID()
	{
		return (PScheduledEventOID)super.getOID();
	}
}
