package ch.shaktipat.saraswati.internal.pool;

import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;


public interface PersistentSharedObjectPool extends PersistentObjectPool
{
	public PSharedObjectOID createPersistentSharedObject( String name, Object shareableObject, PProcessOID owningProcessOID );
}
