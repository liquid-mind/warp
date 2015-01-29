package ch.shaktipat.saraswati.internal.pobject;

import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface PersistentOwnableObject extends PersistentObject
{
	@Override
	public POwnableObjectOID getOID();
	public PProcessOID getOwningProcessOID();
	public void setOwningProcessOID( PProcessOID owningProcessOID );
}
