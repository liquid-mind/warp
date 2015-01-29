package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.util.List;

import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectListHelper;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public abstract class PersistentOwnableObjectImpl extends PersistentObjectImpl implements PersistentOwnableObject, POwnableObject
{
	private static final long serialVersionUID = 1L;

	private PProcessOID owningProcessOID;

	public PersistentOwnableObjectImpl( String name, boolean isPersistent, boolean isSwappable, PProcessOID owningProcessOID )
	{
		super( name, isPersistent, isSwappable );
		setupOwnershipAssociation( owningProcessOID );
	}
	
	private void setupOwnershipAssociation( PProcessOID owningProcessOID )
	{
		PersistentProcess owningProcess = null;
		PProcessOID currentProcessOID = PersistentProcessImpl.getCurrentProcessOID();
		
		if ( currentProcessOID != null && currentProcessOID.equals( owningProcessOID ) )
			owningProcess = PersistentProcessImpl.getSelfProxyCurrentProcess();
		else if ( owningProcessOID != null )
			owningProcess = PersistentProcessImpl.getOtherProxy( owningProcessOID );
		else
			return;
			
		owningProcess.addOwnableObjectOID( getOID() );
		setOwningProcessOID( owningProcessOID );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PProcessOID getOwningProcessOID()
	{
		return owningProcessOID;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setOwningProcessOID( PProcessOID owningProcessOID )
	{
		this.owningProcessOID = owningProcessOID;
	}
	
	public static PProcessOID deriveOwningProcessOIDFromScope( Scope scope )
	{
		PProcessOID owningProcessOID = null;
		
		if ( scope.equals( Scope.PROCESS ) )
		{
			owningProcessOID = PersistentProcessImpl.getCurrentProcessOID();

			if ( owningProcessOID == null )
				throw new RuntimeException( "Cannot create a persistent object with process scope outside of a persistent process." );
		}

		return owningProcessOID;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PProcessHandle getOwningProcess()
	{
		return PersistentProcessImpl.createPProcessHandleProxy( owningProcessOID );
	}

	public static List< POwnableObjectOID > findOIDsByOwningProcess( PProcessOID owningProcessOID, Class< ? > persistentObjectType )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		List< POwnableObjectOID > oidIterator = omniObjectPool.findByOwningProcess( owningProcessOID, persistentObjectType );
		
		return oidIterator;
	}

	public static List< POwnableObject > findByOwningProcess( PProcessOID owningProcessOID )
	{
		List< POwnableObjectOID > oidList = PersistentOwnableObjectImpl.findOIDsByOwningProcess( owningProcessOID, PersistentOwnableObjectImpl.class );
		List< POwnableObject > pProcessHandleList = PersistentObjectListHelper.createExternalProxyList( POwnableObject.class, oidList );
		
		return pProcessHandleList;
	}

	@Override
	public POwnableObjectOID getOID()
	{
		return (POwnableObjectOID)super.getOID();
	}
}
