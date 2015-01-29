package ch.shaktipat.saraswati.pobject.manager;

import java.util.List;

import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public interface PSharedObjectManager extends POwnableObjectManager
{
	public PSharedObjectHandle create( Object shareableObject );
	public PSharedObjectHandle create( String name, Object shareableObject );
	public PSharedObjectHandle create( String name, Object shareableObject, PProcessOID owningProcessOID );
	public PSharedObjectHandle getHandle( PSharedObjectOID pSharedObjectOID );

	public PSharedObjectHandle findByOID( PSharedObjectOID oid );
	public List< PSharedObjectHandle > findByName( String name );	
}
