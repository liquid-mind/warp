package ch.shaktipat.saraswati.pobject;

import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public interface PSharedObjectCommon extends POwnableObject
{
	@Override
	public PSharedObjectOID getOID();
	public Class< ? > getSharedObjectClass();
}
