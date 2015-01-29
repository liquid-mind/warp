package ch.shaktipat.saraswati.pobject;

import java.util.UUID;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public interface PEventTopic extends POwnableObject
{
	@Override
	public PEventTopicOID getOID();
	public void publish( Event event );
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription );
	public boolean unsubscribe( UUID subscriptionOID );
}
