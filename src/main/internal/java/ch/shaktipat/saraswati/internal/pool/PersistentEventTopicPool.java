package ch.shaktipat.saraswati.internal.pool;

import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface PersistentEventTopicPool extends PersistentObjectPool
{
	public PEventTopicOID createPersistentEventTopic( String name, boolean isPersistent, PProcessOID owningProcessOID );
}
