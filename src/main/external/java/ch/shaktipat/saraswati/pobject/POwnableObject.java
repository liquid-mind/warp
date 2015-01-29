package ch.shaktipat.saraswati.pobject;

import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;

public interface POwnableObject extends PObject
{
	@Override
	public POwnableObjectOID getOID();
	public PProcessHandle getOwningProcess();
}
