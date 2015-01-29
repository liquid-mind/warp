package ch.shaktipat.saraswati.internal.pool;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PersistentObjectNotFoundExeception extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public PersistentObjectNotFoundExeception( PObjectOID pObjectOID )
	{
		super( "Persistent object not found. OID=" + pObjectOID );
	}
}
