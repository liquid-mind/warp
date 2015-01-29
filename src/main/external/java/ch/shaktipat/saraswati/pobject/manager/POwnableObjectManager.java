package ch.shaktipat.saraswati.pobject.manager;

import java.util.List;

import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface POwnableObjectManager extends PObjectManager
{
	public List< POwnableObject > findByOwningProcess( PProcessOID owningProcessOID );	
	public PProcessHandle findByOwnableObject( POwnableObjectOID ownableObjectOID );	
}
