package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentEventTopicInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectListHelper;
import ch.shaktipat.saraswati.internal.pobject.aux.event.EventFilter;
import ch.shaktipat.saraswati.internal.pobject.aux.event.Subscription;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.pool.PersistentObjectNotFoundExeception;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PersistentEventTopicImpl extends PersistentOwnableObjectImpl implements PersistentEventTopic, PEventTopic
{
	private static final long serialVersionUID = 1L;
	
	private Set< Subscription > subscriptions = new HashSet< Subscription >();
	
	public PersistentEventTopicImpl( String name, boolean persistent, PProcessOID owningProcessOID )
	{
		// If this object is persistent then it is also swappable; otherwise not.
		super( name, persistent, persistent, owningProcessOID );
	}

	@Override
	@ProxyMethod( synchronicity = Synchronicity.SYNCHRONOUS )
	public String getDefaultName()
	{
		return PersistentEventTopic.class.getSimpleName() + "-" + getOID();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public void publish( Event event )
	{
		event.setSenderOID( PersistentProcessImpl.getCurrentProcessOID() );

		Iterator< Subscription > iter = subscriptions.iterator();
		
		// Iterate through all subscriptions.
		while ( iter.hasNext() )
		{
			Subscription subscription = iter.next();
			
			// Does this subscription match the event?
			if ( subscription.getEventFilter().matches( event ) )
			{
				event.setSubscriptionOID( subscription.getSubscriptionOID() );
				
				try
				{
					// Send event to subscriber (note that event is cloned as a result
					// of invoking send() since peQueue employs a NON_SHARED memory model;
					// therefore, there we can simply set a new subscription OID on each loop)
					PersistentEventQueue peQueue = PersistentEventQueueImpl.getOtherProxy( subscription.getTargetEventQueueOID() );
					peQueue.send( event );
				}
				catch ( PersistentObjectNotFoundExeception e )
				{
					// Ignore this exception, since event delivery is best effort only. There are cases
					// in which an event queue that subscribed to a topic may not exist when the event
					// is actually delivered. Some cases include:
					// 1. The event queue received a timeout event rather than the event it was subscribed
					//    to wait on --> dangling subscription. The queue is then subsequently destroyed
					//    before the subscription can be consumed.
					// 2. The user subscribed to a topic and forget to either unsubscribe or listen for
					//    the event before destroying the queue.
				}
				
				// If subscription is oneTimeOnly --> remove it.
				if ( subscription.isOneTimeOnly() )
					iter.remove();
			}
		}
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription )
	{
		EventFilter eventFilter = new EventFilter( filterExpr );
		Subscription subscription = new Subscription( targetQueueOID, eventFilter, singleEventSubscription );
		subscriptions.add( subscription );
		
		return subscription.getSubscriptionOID();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public boolean unsubscribe( UUID subscriptionOID )
	{
		Iterator< Subscription > iter = subscriptions.iterator();
		boolean success = false;
		
		// Find the subscription with OID == subscriptionOID and remove it.
		while ( iter.hasNext() )
		{
			Subscription subscription = iter.next();
			
			if ( subscription.getSubscriptionOID().equals( subscriptionOID ) )
			{
				iter.remove();
				success = true;
			}
		}

		return success;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Set< Subscription > getSubscriptions()
	{
		return subscriptions;
	}
	
	///////////////
	// FIND METHODS
	///////////////
	
	public static PEventTopic findByOID( PEventTopicOID oid )
	{
		return createPEventTopicProxy( oid );
	}
	
	@SuppressWarnings( "unchecked" )
	public static List< PEventTopic > findByName( String name )
	{
		List< PEventTopicOID > oids = (List< PEventTopicOID >)(Object)findOIDsByName( name, PersistentEventTopicImpl.class );
		List< PEventTopic > pEventTopics = PersistentObjectListHelper.createExternalProxyList( PEventTopic.class, oids );
		
		return pEventTopics;
	}

	/////////////////
	// CREATE METHODS
	/////////////////
	
	public static PEventTopic create()
	{
		return create( null );
	}
	
	public static PEventTopic create( Scope scope )
	{
		return create( null, Scope.GLOBAL );
	}
	
	public static PEventTopic create( String name, Scope scope )
	{
		boolean isPersistent = ( PersistentProcessImpl.getCurrentProcessOID() != null );
		PEventTopic pEventTopic = create( name, isPersistent, PersistentOwnableObjectImpl.deriveOwningProcessOIDFromScope( scope ) );
		
		return pEventTopic;
	}
	
	public static PEventTopic create( String name, boolean isPersistent, PProcessOID owningProcessOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PEventTopicOID pEventTopicOID = omniObjectPool.createPersistentEventTopic( name, isPersistent, owningProcessOID );
		PEventTopic pEventTopic = createPEventTopicProxy( pEventTopicOID );
		
		return pEventTopic;
	}
	
	////////////////
	// PROXY METHODS
	////////////////
	
	@Override
	public PersistentEventTopic getSelfProxy()
	{
		return (PersistentEventTopic)super.getSelfProxy();
	}

	@Override
	public PersistentEventTopic getOtherProxy()
	{
		return (PersistentEventTopic)super.getOtherProxy();
	}

	public static PersistentEventTopic getOtherProxy( PEventTopicOID pEventTopicOID )
	{
		return (PersistentEventTopic)getProxy( ProxyType.INTERNAL_OTHER, pEventTopicOID );
	}
	
	@Override
	protected PersistentEventTopic createSelfProxy()
	{
		return createProxy( ProxyType.INTERNAL_SELF, getOID() );
	}

	@Override
	protected PersistentEventTopic createOtherProxy()
	{
		return createProxy( ProxyType.INTERNAL_OTHER, getOID() );
	}
	
	public static PEventTopic createPEventTopicProxy( PEventTopicOID pEventTopicOID )
	{
		return createProxy( ProxyType.EXTERNAL_OTHER, pEventTopicOID );
	}
	
	private static < T > T createProxy( ProxyType proxyType, PEventTopicOID pEventTopicOID )
	{
		return createProxy( proxyType, pEventTopicOID, PersistentEventTopic.class, PEventTopic.class, PEventTopic.class, new PersistentEventTopicInvocationHandler() );
	}

	@Override
	public PEventTopicOID getOID()
	{
		return (PEventTopicOID)super.getOID();
	}
}
