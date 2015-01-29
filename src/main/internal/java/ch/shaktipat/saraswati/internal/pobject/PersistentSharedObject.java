package ch.shaktipat.saraswati.internal.pobject;

import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;


public interface PersistentSharedObject extends PersistentOwnableObject
{
	@Override
	public PSharedObjectOID getOID();
	public < T > T get();
	public < T > T getWithoutProxy();
	@Override
	public PersistentSharedObject getSelfProxy();
	@Override
	public PersistentSharedObject getOtherProxy();
}
