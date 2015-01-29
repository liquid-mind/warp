package ch.shaktipat.saraswati.internal.dynproxies;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.common.SaraswatiInternalError;
import ch.shaktipat.saraswati.internal.common.SerializationHelper;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

@SuppressWarnings( "unused" )
public abstract class PersistentObjectInvoker implements Runnable, Callable< Object >
{
	private static Logger logger = Logger.getLogger( PersistentObjectInvoker.class.getName() );

	public enum MemoryModel { SHARED, NON_SHARED };
	
	private PObjectOID targetOID;
	private MemoryModel memoryModel;
	private Synchronicity synchronicity;
	private Object proxy;
	private Method method;
	private Object[] args;

	public PersistentObjectInvoker( PObjectOID targetOID, MemoryModel memoryModel )
	{
		super();
		this.targetOID = targetOID;
		this.memoryModel = memoryModel;
	}

	@Override
	public void run()
	{
		try
		{
			call();
		}
		catch ( Exception e )
		{
			logger.log( Level.SEVERE, "", e );
		}
	}

	@Override
	public Object call() throws Exception
	{
		Object retVal = null;
		
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PersistentObject persistentObject = omniObjectPool.borrowPersistentObject( targetOID, false );
		
		try
		{
			Object targetObject = getTargetObject( persistentObject );
			ProxyMethod proxyMethod = getProxyMethod( method );
			retVal = invoke( persistentObject, targetObject, method, proxyMethod, args );
		}
		catch ( Throwable t )
		{
			if ( synchronicity.equals( Synchronicity.SYNCHRONOUS ) )
				throw t;
			
			logger.log( Level.SEVERE, "", t );
		}
		finally
		{
			omniObjectPool.returnPersistentObject( persistentObject, false );
		}
		
		return retVal;
	}
	
	public ProxyMethod getProxyMethod( Method method )
	{
		ProxyMethod proxyMethod = null;
		
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PersistentObject persistentObject = omniObjectPool.borrowPersistentObject( targetOID, false );
		
		try
		{
			Object targetObject = getTargetObject( persistentObject );
			Method actualMethod = ClassWrapper.getMethod( targetObject.getClass(), method.getName(), method.getParameterTypes() );
			proxyMethod = actualMethod.getAnnotation( ProxyMethod.class );
		}
		finally
		{
			omniObjectPool.returnPersistentObject( persistentObject, false );
		}
		
		return proxyMethod;
	}
	
	protected Object invoke( PersistentObject persistentObject, Object targetObject, Method method, ProxyMethod proxyMethod, Object[] args ) throws Exception
	{
		Object retVal = null;
		
		StateProtection stateProtection = proxyMethod.stateProtection();

		if ( targetObject instanceof PersistentObject )
			lock( (PersistentObject)targetObject, stateProtection );
		
		try
		{
			retVal = invokeWithInvocationTargetExceptionHandler( method, proxyMethod, targetObject, args );
		}
		finally
		{
			if ( targetObject instanceof PersistentObject )
				unlock( (PersistentObject)targetObject, stateProtection );
		}
		
		return retVal;
	}
	
	private Object invokeWithInvocationTargetExceptionHandler( Method method, ProxyMethod proxyMethod, Object targetObject, Object[] args ) throws Exception
	{
		Object targetRetVal = null;
		
		try
		{
			Object[] targetArgs = enforceMemoryModel( args );
			Object retVal = method.invoke( targetObject, targetArgs );
			targetRetVal = enforceMemoryModel( retVal );
		}
		catch ( InvocationTargetException ite )
		{
			// An InvocationTargetException signifies an error in the target object,
			// which must simply be passed through untouched.
			handleInvocationTargetExceptionHandler( ite );
		}
		catch ( Throwable t )
		{
			// Anything else signifies an error in the dynamic proxy itself, which
			// must be regarded as an internal error.
			throw new SaraswatiInternalError( t );
		}
		
		return targetRetVal;
	}
	
	private void handleInvocationTargetExceptionHandler( InvocationTargetException ite ) throws Exception
	{
		Throwable causingException = null;

		if ( ite.getCause() != null )
		{
			causingException = ite.getCause();
		}
		else if ( ite.getTargetException() != null )
		{
			causingException = ite.getTargetException();
		}
		else
		{
			throw new IllegalStateException( "No cause and no target exception found for InvocationTargetException." );
		}
		
		if ( causingException instanceof Error )
			throw (Error)causingException;
		else
			throw (Exception)causingException;
	}	
	
	public Object getTargetObject( PersistentObject persistentObject )
	{
		return persistentObject;
	}
	
	private void lock( PersistentObject persistentObject, StateProtection protection )
	{
		if ( protection.equals( STATE_READ_LOCK ) )
			persistentObject.getStateReadLock().lock();
		else if ( protection.equals( STATE_WRITE_LOCK ) )
			persistentObject.getStateWriteLock().lock();
	}
	
	private void unlock( PersistentObject persistentObject, StateProtection protection )
	{
		if ( protection.equals( STATE_READ_LOCK ) )
			persistentObject.getStateReadLock().unlock();
		else if ( protection.equals( STATE_WRITE_LOCK ) )
			persistentObject.getStateWriteLock().unlock();
	}

	public Object[] enforceMemoryModel( Object[] sourceObjects )
	{
		Object[] targetObjects = new Object[ sourceObjects.length ];
		
		for ( int i = 0 ; i < sourceObjects.length ; ++i )
			targetObjects[ i ] = enforceMemoryModel( sourceObjects[ i ] );
		
		return targetObjects;
	}
	
	public Object enforceMemoryModel( Object sourceObject )
	{
		Object targetObject = null;
		
		if ( memoryModel.equals( MemoryModel.SHARED ) )
			targetObject = sourceObject;
		else if ( memoryModel.equals( MemoryModel.NON_SHARED ) )
			targetObject = SerializationHelper.cloneObject( sourceObject );
		else
			throw new IllegalStateException( "Unexpected value for memoryModel: " + memoryModel );
		
		return targetObject;
	}

	public void setProxy( Object proxy )
	{
		this.proxy = proxy;
	}

	public void setMethod( Method method )
	{
		this.method = method;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs( Object[] args )
	{
		this.args = args;
	}

	public void setSynchronicity( Synchronicity synchronicity )
	{
		this.synchronicity = synchronicity;
	}

	public PObjectOID getTargetOID()
	{
		return targetOID;
	}
}
