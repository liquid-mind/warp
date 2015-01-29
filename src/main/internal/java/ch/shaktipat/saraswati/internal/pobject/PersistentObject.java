package ch.shaktipat.saraswati.internal.pobject;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public interface PersistentObject extends Serializable
{
	public PObjectOID getOID();
	public void destroy();
	public boolean exists();
	public ReadLock getReferenceReadLock();
	public WriteLock getReferenceWriteLock();
	public ReadLock getStateReadLock();
	public WriteLock getStateWriteLock();
	public boolean isReferencableInstance();
	public void setReferencableInstance( boolean isValidInstance );
	public PersistentObject getSelfProxy();
	public PersistentObject getOtherProxy();
	public Date getCreateDate();
	public Date getLastActivityDate();
	public void setLastActivityDate( Date lastActivityDate );
	public boolean isPersistent();
	public boolean isSwappable();
	public String getName();
	public void setName( String name );
	public String getDefaultName();
	public int getLockHoldCount();
	public void notifyActivate();
	public void notifyPassivate();
	public void notifyDestroy();
}
