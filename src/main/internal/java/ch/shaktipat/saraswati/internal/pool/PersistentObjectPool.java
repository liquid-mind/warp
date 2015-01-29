package ch.shaktipat.saraswati.internal.pool;

import java.util.Date;
import java.util.List;

import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectVisitor;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface PersistentObjectPool
{
	public void addPersistentObject( PersistentObject persistentObject );
	public void removePersistentObject( PObjectOID persistentObjectOID );
	public PersistentObject borrowPersistentObject( PObjectOID persistentObjectOID, boolean exclusiveAccess );
	public void returnPersistentObject( PersistentObject persistentObject, boolean exclusiveAccess );
	public boolean persistentObjectExists( PObjectOID persistentObjectOID );
	public void destroyPersistentObject( PObjectOID persistentObjectOID );
	public boolean visitPersistentObjects( PersistentObjectVisitor visitor, Class< ? > persistentObjectType );
	public void start();
	public void stop();
	public PersistentObject load( PObjectOID persistentObjectOID );
	public void save( PObjectOID persistentObjectOID );
	public PersistentObject activate( PObjectOID persistentObjectOID );
	public void passivate( PObjectOID persistentObjectOID );
	public PersistentObject getPersistentObject( PObjectOID persistentObjectOID );
	public List< PObjectOID > findByName( String name, Class< ? > persistentObjectType );
	public List< PObjectOID > findByCreateDate( Date createDateLowerBoundary, Date createDateUpperBoundary, Class< ? > persistentObjectType );
	public List< PObjectOID > findByLastActivityDate( Date lastActivityDateLowerBoundary, Date lastActivityDateUpperBoundary, Class< ? > persistentObjectType );
	public List< POwnableObjectOID > findByOwningProcess( PProcessOID owningProcessOID, Class< ? > persistentObjectType );
	public PProcessOID findByOwnableObject( POwnableObjectOID ownableObjectOID );
}
