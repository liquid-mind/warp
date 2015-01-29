package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.util.List;

import ch.shaktipat.saraswati.internal.dynproxies.PersistentSharedObjectInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectListHelper;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.PSharedObject;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public class PersistentSharedObjectImpl extends PersistentOwnableObjectImpl implements PSharedObjectHandle, PersistentSharedObject, PSharedObject
{
	private static final long serialVersionUID = 1L;

	private static ThreadLocal< PSharedObjectOID > currentSharedObject = new ThreadLocal< PSharedObjectOID >();
	
	private Object sharedObject;

	public PersistentSharedObjectImpl( String name, PProcessOID owningProcessOID, Object sharedObject )
	{
		super( name, true, true, owningProcessOID );
		this.sharedObject = sharedObject;
	}

	// Special method; implemented in proxy
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=StateProtection.STATE_READ_LOCK )
	public < T > T get() { throw new UnsupportedOperationException(); }

	@SuppressWarnings( "unchecked" )
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=StateProtection.STATE_READ_LOCK )
	public < T > T getWithoutProxy()
	{
		return (T)sharedObject;
	}

	public static PSharedObjectOID getCurrentObjectOID()
	{
		return currentSharedObject.get();
	}
	
	public static void setCurrentObjectOID( PSharedObjectOID pSharedObjectOID )
	{
		currentSharedObject.set( pSharedObjectOID );
	}

	@Override
	public String getDefaultName()
	{
		return PersistentSharedObject.class.getSimpleName() + "-" + getOID();
	}
	
	@Override
	public boolean isPersistent()
	{
		return true;
	}

	@Override
	public PSharedObjectHandle getPSharedObjectHandle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=StateProtection.STATE_READ_LOCK )
	public Class< ? > getSharedObjectClass()
	{
		return sharedObject.getClass();
	}
	
	///////////////
	// FIND METHODS
	///////////////
	
	public static PSharedObjectHandle findByOID( PSharedObjectOID oid )
	{
		return createPSharedObjectHandleProxy( oid );
	}
	
	@SuppressWarnings( "unchecked" )
	public static List< PSharedObjectHandle > findByName( String name )
	{
		List< PSharedObjectOID > oids = (List< PSharedObjectOID >)(Object)findOIDsByName( name, PersistentSharedObjectImpl.class );
		List< PSharedObjectHandle > pSharedObjects = PersistentObjectListHelper.createExternalProxyList( PSharedObjectHandle.class, oids );
		
		return pSharedObjects;
	}
	
	/////////////////
	// CREATE METHODS
	/////////////////
	
	public static PSharedObjectHandle create( Object shareableObject )
	{
		return create( null, shareableObject );
	}
	
	public static PSharedObjectHandle create( String name, Object shareableObject )
	{
		return create( name, shareableObject, null );
	}
	
	public static PSharedObjectHandle create( String name, Object shareableObject, PProcessOID owningProcessOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PSharedObjectOID persistentSharedObjectOID = omniObjectPool.createPersistentSharedObject( name, shareableObject, owningProcessOID );
		PSharedObjectHandle pSharedObjectHandle = createPSharedObjectHandleProxy( persistentSharedObjectOID );
		
		return pSharedObjectHandle;
	}

	////////////////
	// PROXY METHODS
	////////////////
	
	@Override
	public PersistentSharedObject getSelfProxy()
	{
		return (PersistentSharedObject)super.getSelfProxy();
	}

	@Override
	public PersistentSharedObject getOtherProxy()
	{
		return (PersistentSharedObject)super.getOtherProxy();
	}

	public static PersistentSharedObject getOtherProxy( PSharedObjectOID pSharedObjectOID )
	{
		return (PersistentSharedObject)getProxy( ProxyType.INTERNAL_OTHER, pSharedObjectOID );
	}
	
	@Override
	protected PersistentSharedObject createSelfProxy()
	{
		return createProxy( ProxyType.INTERNAL_SELF, getOID() );
	}

	@Override
	protected PersistentSharedObject createOtherProxy()
	{
		return createProxy( ProxyType.INTERNAL_OTHER, getOID() );
	}

	public static PSharedObject createPSharedObjectProxy()
	{
		PSharedObjectOID pSharedObjectOID = currentSharedObject.get();
		PSharedObject pSharedObjectProxy = null;
		
		if ( pSharedObjectOID != null )
			pSharedObjectProxy = createProxy( ProxyType.EXTERNAL_SELF, pSharedObjectOID );
		
		return pSharedObjectProxy;
	}
	
	public static PSharedObjectHandle createPSharedObjectHandleProxy( PSharedObjectOID pSharedObjectOID )
	{
		return createProxy( ProxyType.EXTERNAL_OTHER, pSharedObjectOID );
	}
	
	private static < T > T createProxy( ProxyType proxyType, PSharedObjectOID pSharedObjectOID )
	{
		return createProxy( proxyType, pSharedObjectOID, PersistentSharedObject.class, PSharedObject.class, PSharedObjectHandle.class, new PersistentSharedObjectInvocationHandler( false ) );
	}

	@Override
	public PSharedObjectOID getOID()
	{
		return (PSharedObjectOID)super.getOID();
	}
}
