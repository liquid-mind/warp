package ch.shaktipat.saraswati.internal.pool;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface PersistentEventQueuePool extends PersistentObjectPool
{
	public PEventQueueOID createPersistentEventQueue( String name, boolean isPersistent, PProcessOID owningProcessOID );
}
