package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.shaktipat.saraswati.internal.dynproxies.PersistentObjectInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentObjectInvoker.MemoryModel;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public abstract class PersistentObjectImpl implements PersistentObject, PObject
{
	private static final long serialVersionUID = 1L;
	
	private ReentrantReadWriteLock referenceLock;
	private ReentrantReadWriteLock stateLock;
	private ReadLock referenceReadLock;
	private WriteLock referenceWriteLock;
	private ReadLock stateReadLock;
	private WriteLock stateWriteLock;

	// Protected by: referenceLock.
	private boolean isReferencableInstance;

	// Protected by: stateLock.
	private PObjectOID oid;
	private Date createDate;
	private Date lastActivityDate;
	private String name;
	private boolean isPersistent;
	private boolean isSwappable;

	// Intrinsically thread-safe.
	private PersistentObject selfProxy;
	private PersistentObject otherProxy;
	
	public PersistentObjectImpl( String name, boolean isPersistent, boolean isSwappable )
	{
		super();
		oid = PObjectOIDFactory.create( this.getClass() );
		referenceLock = new ReentrantReadWriteLock();
		stateLock = new ReentrantReadWriteLock();
		referenceReadLock = referenceLock.readLock();
		referenceWriteLock = referenceLock.writeLock();
		stateReadLock = stateLock.readLock();
		stateWriteLock = stateLock.writeLock();
		isReferencableInstance = true;
		createDate = new Date();
		lastActivityDate = new Date();
		this.name = setupName( name );
		this.isPersistent = isPersistent;
		this.isSwappable = isSwappable;
	}
	
	private String setupName( String name )
	{
		String finalName = null;
		
		if ( name == null )
			finalName = getDefaultName();
		else
			finalName = name;
		
		return finalName;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PObjectOID getOID()
	{
		return oid;
	}

	// Special methods; implemented directly in dynamic proxy.
	
	@Override
	public void destroy() { throw new UnsupportedOperationException(); }

	@Override
	public boolean exists()	{ throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public ReadLock getReferenceReadLock()
	{
		return referenceReadLock;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public WriteLock getReferenceWriteLock()
	{
		return referenceWriteLock;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public ReadLock getStateReadLock()
	{
		return stateReadLock;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public WriteLock getStateWriteLock()
	{
		return stateWriteLock;
	}

	// Note that isValidInstance is protected by the referenceLock.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public boolean isReferencableInstance()
	{
		return isReferencableInstance;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void setReferencableInstance( boolean isReferencableInstance )
	{
		this.isReferencableInstance = isReferencableInstance;
	}

	@Override
	public PersistentObject getSelfProxy()
	{
		if ( selfProxy == null )
			selfProxy = createSelfProxy();
	
		return selfProxy;
	}

	@Override
	public PersistentObject getOtherProxy()
	{
		if ( otherProxy == null )
			otherProxy = createOtherProxy();
	
		return otherProxy;
	}
	
	protected abstract PersistentObject createSelfProxy();
	protected abstract PersistentObject createOtherProxy();

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Date getCreateDate()
	{
		return createDate;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Date getLastActivityDate()
	{
		return lastActivityDate;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setLastActivityDate( Date lastActivityDate )
	{
		this.lastActivityDate = lastActivityDate;
	}
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public String getName()
	{
		return name;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setName( String name )
	{
		this.name = name;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public abstract String getDefaultName();
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean isPersistent()
	{
		return isPersistent;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean isSwappable()
	{
		return isSwappable;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void notifyActivate() {};

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void notifyPassivate() {};

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void notifyDestroy() {};
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( oid == null ) ? 0 : oid.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		PersistentObjectImpl other = (PersistentObjectImpl)obj;
		if ( oid == null )
		{
			if ( other.oid != null )
				return false;
		}
		else if ( !oid.equals( other.oid ) )
			return false;
		return true;
	}

	///////////////
	// FIND METHODS
	///////////////
	
	public static List< PObjectOID > findOIDsByName( String name, Class< ? > persistentObjectType )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		List< PObjectOID > oidIterator = omniObjectPool.findByName( name, persistentObjectType );
		
		return oidIterator;
	}
	
	public static List< PObjectOID > findOIDsByCreateDate( Date createDateLowerBoundary, Date createDateUpperBoundary, Class< ? > persistentObjectType )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		List< PObjectOID > pProcessOIDIterator = omniObjectPool.findByCreateDate( createDateLowerBoundary, createDateUpperBoundary, persistentObjectType );
		
		return pProcessOIDIterator;
	}
	
	public static List< PObjectOID > findOIDsByLastActivityDate( Date lastActivityDateLowerBoundary, Date lastActivityDateUpperBoundary, Class< ? > persistentObjectType )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		List< PObjectOID > pProcessOIDIterator = omniObjectPool.findByLastActivityDate( lastActivityDateLowerBoundary, lastActivityDateUpperBoundary, persistentObjectType );
		
		return pProcessOIDIterator;
	}	
	
	////////////////
	// PROXY METHODS
	////////////////
	
	public static PersistentObject getProxy( ProxyType proxyType, PObjectOID pObjectOID )
	{
		if ( pObjectOID == null )
			return null;
		
		PersistentObject proxy = null;
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		
		PersistentObject pObject = omniObjectPool.borrowPersistentObject( pObjectOID, false );
		
		try
		{
			if ( proxyType.equals( ProxyType.INTERNAL_SELF ) )
				proxy = pObject.getSelfProxy();
			else if ( proxyType.equals( ProxyType.INTERNAL_OTHER ) )
				proxy = pObject.getOtherProxy();
			else
				throw new IllegalStateException( "Unexpected type for proxyType: " + proxyType );
		}
		finally
		{
			omniObjectPool.returnPersistentObject( pObject, false );
		}
		
		return proxy;
	}

	@SuppressWarnings( "unchecked" )
	public static < T > T createProxy( ProxyType proxyType, PObjectOID pObjectOID, Class< ? > selfInterface, Class< ? > externalSelfInterface, Class< ? > externalOtherInterface, PersistentObjectInvocationHandler handler )
	{
		if ( pObjectOID == null )
			return null;
		
		Class< ? > proxyInterface = null;
		MemoryModel memoryModel = null;
		
		if ( proxyType.equals( ProxyType.INTERNAL_SELF ) )
		{
			proxyInterface = selfInterface;
			memoryModel = MemoryModel.SHARED;
		}
		else if ( proxyType.equals( ProxyType.INTERNAL_OTHER ) )
		{
			proxyInterface = selfInterface;
			memoryModel = MemoryModel.NON_SHARED;
		}
		else if ( proxyType.equals( ProxyType.EXTERNAL_SELF ) )
		{
			proxyInterface = externalSelfInterface;
			memoryModel = MemoryModel.SHARED;
		}
		else if ( proxyType.equals( ProxyType.EXTERNAL_OTHER ) )
		{
			proxyInterface = externalOtherInterface;
			memoryModel = MemoryModel.NON_SHARED;
		}
		else
			throw new IllegalStateException( "Unexpected type for proxyType:" + proxyType );
		
		handler.setTargetOID( pObjectOID );
		handler.setMemoryModel( memoryModel );
		T proxy = (T)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class[] { proxyInterface }, handler );
		
		return proxy;
	}

	@Override
	public int getLockHoldCount()
	{
		int lockHoldCount = 0;
		
		if ( referenceLock.isWriteLocked() )
			lockHoldCount += 1;
		
		lockHoldCount += referenceLock.getReadLockCount();
		
		return lockHoldCount;
	}
}
