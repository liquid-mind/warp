package ch.shaktipat.saraswati.internal.pobject;

import java.util.Set;
import java.util.UUID;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.internal.pobject.aux.event.Subscription;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public interface PersistentEventTopic extends PersistentOwnableObject
{
	@Override
	public PEventTopicOID getOID();
	public void publish( Event event );
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription );
	public boolean unsubscribe( UUID subscriptionOID );
	public Set< Subscription > getSubscriptions();
	@Override
	public PersistentEventTopic getSelfProxy();
	@Override
	public PersistentEventTopic getOtherProxy();
}
