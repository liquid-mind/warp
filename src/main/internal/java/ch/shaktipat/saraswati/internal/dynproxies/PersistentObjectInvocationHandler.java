package ch.shaktipat.saraswati.internal.dynproxies;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.ASYNCHRONOUS;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentObjectInvoker.MemoryModel;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopic;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentOwnableObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObject;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.LocalFuture;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessCommon;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PScheduledEvent;
import ch.shaktipat.saraswati.pobject.PSharedObjectCommon;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(
	value="UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
	justification="Object state is set after construction; needs to be refactored."
	)
public abstract class PersistentObjectInvocationHandler implements InvocationHandler, Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Method GET_OID1_METHOD = ClassWrapper.getDeclaredMethod( PObject.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID2_METHOD = ClassWrapper.getDeclaredMethod( POwnableObject.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID3_METHOD = ClassWrapper.getDeclaredMethod( PProcessCommon.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID4_METHOD = ClassWrapper.getDeclaredMethod( PEventQueue.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID5_METHOD = ClassWrapper.getDeclaredMethod( PEventTopic.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID6_METHOD = ClassWrapper.getDeclaredMethod( PScheduledEvent.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID7_METHOD = ClassWrapper.getDeclaredMethod( PSharedObjectCommon.class, FieldAndMethodConstants.GET_OID_METHOD );

	private static final Method GET_OID8_METHOD = ClassWrapper.getDeclaredMethod( PersistentObject.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID9_METHOD = ClassWrapper.getDeclaredMethod( PersistentOwnableObject.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID10_METHOD = ClassWrapper.getDeclaredMethod( PersistentProcess.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID11_METHOD = ClassWrapper.getDeclaredMethod( PersistentEventQueue.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID12_METHOD = ClassWrapper.getDeclaredMethod( PersistentEventTopic.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID13_METHOD = ClassWrapper.getDeclaredMethod( PersistentScheduledEvent.class, FieldAndMethodConstants.GET_OID_METHOD );
	private static final Method GET_OID14_METHOD = ClassWrapper.getDeclaredMethod( PersistentSharedObject.class, FieldAndMethodConstants.GET_OID_METHOD );
	
	private static final Method GET_FUTURE_METHOD = ClassWrapper.getDeclaredMethod( PProcessHandle.class, FieldAndMethodConstants.GET_FUTURE_METHOD );
	private static final Method EXISTS1_METHOD = ClassWrapper.getDeclaredMethod( PObject.class, FieldAndMethodConstants.EXISTS_METHOD );
	private static final Method EXISTS2_METHOD = ClassWrapper.getDeclaredMethod( PersistentObject.class, FieldAndMethodConstants.EXISTS_METHOD );
	private static final Method DESTROY1_METHOD = ClassWrapper.getDeclaredMethod( PObject.class, FieldAndMethodConstants.DESTROY_METHOD );
	private static final Method DESTROY2_METHOD = ClassWrapper.getDeclaredMethod( PersistentObject.class, FieldAndMethodConstants.DESTROY_METHOD );
	private static final Method EQUALS_METHOD = ClassWrapper.getDeclaredMethod( Object.class, FieldAndMethodConstants.EQUALS_METHOD, Object.class );
	private static final Method HASH_CODE_METHOD = ClassWrapper.getDeclaredMethod( Object.class, FieldAndMethodConstants.HASH_CODE_METHOD );
	private static final Method TO_STRING_METHOD = ClassWrapper.getDeclaredMethod( Object.class, FieldAndMethodConstants.TO_STRING_METHOD );
	private static final Set< Method > GET_OID_METHODS = new HashSet< Method >();
	private static final Set< Method > EXISTS_METHODS = new HashSet< Method >();
	private static final Set< Method > DESTROY_METHODS = new HashSet< Method >();
	private static final Set< Method > SPECIAL_METHODS = new HashSet< Method >();
	
	static
	{
		GET_OID_METHODS.add( GET_OID1_METHOD );
		GET_OID_METHODS.add( GET_OID2_METHOD );
		GET_OID_METHODS.add( GET_OID3_METHOD );
		GET_OID_METHODS.add( GET_OID4_METHOD );
		GET_OID_METHODS.add( GET_OID5_METHOD );
		GET_OID_METHODS.add( GET_OID6_METHOD );
		GET_OID_METHODS.add( GET_OID7_METHOD );
		GET_OID_METHODS.add( GET_OID8_METHOD );
		GET_OID_METHODS.add( GET_OID9_METHOD );
		GET_OID_METHODS.add( GET_OID10_METHOD );
		GET_OID_METHODS.add( GET_OID11_METHOD );
		GET_OID_METHODS.add( GET_OID12_METHOD );
		GET_OID_METHODS.add( GET_OID13_METHOD );
		GET_OID_METHODS.add( GET_OID14_METHOD );
		
		EXISTS_METHODS.add( EXISTS1_METHOD );
		EXISTS_METHODS.add( EXISTS2_METHOD );
		
		DESTROY_METHODS.add( DESTROY1_METHOD );
		DESTROY_METHODS.add( DESTROY2_METHOD );
		
		SPECIAL_METHODS.addAll( GET_OID_METHODS );
		SPECIAL_METHODS.addAll( EXISTS_METHODS );
		SPECIAL_METHODS.addAll( DESTROY_METHODS );
		SPECIAL_METHODS.add( GET_FUTURE_METHOD );
		SPECIAL_METHODS.add( EQUALS_METHOD );
		SPECIAL_METHODS.add( HASH_CODE_METHOD );
		SPECIAL_METHODS.add( TO_STRING_METHOD );
	}
	
	private PObjectOID targetOID;
	private MemoryModel memoryModel;
	
	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object retVal = null;

		if ( SPECIAL_METHODS.contains( method ) )
			retVal = invokeSpecial( proxy, method, args );
		else
			retVal = invokeNormally( proxy, method, args );
		
		return retVal;
	}
	
	public Object invokeSpecial( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object retVal = null;

		if ( GET_OID_METHODS.contains( method ) )
			retVal = invokeGetOID();
		else if ( EXISTS_METHODS.contains( method ) )
			retVal = invokeExists();
		else if ( DESTROY_METHODS.contains( method ) )
			invokeDestroy();
		else if ( method.equals( GET_FUTURE_METHOD ) )
			retVal = invokeGetFuture( (PProcessHandle)proxy );
		else if ( method.equals( EQUALS_METHOD ) )
			retVal = invokeEquals( args[ 0 ] );
		else if ( method.equals( HASH_CODE_METHOD ) )
			retVal = invokeHashCode();
		else if ( method.equals( TO_STRING_METHOD ) )
			retVal = invokeToString();
		
		return retVal;
	}
	
	private Object invokeExists()
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		return omniObjectPool.persistentObjectExists( targetOID );
	}
	
	private void invokeDestroy()
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		omniObjectPool.destroyPersistentObject( targetOID );
	}
	
	private Object invokeGetOID()
	{
		return targetOID;
	}

	public boolean invokeEquals( Object obj )
	{
		if ( !(obj instanceof PObject) )
			return false;
		
		PObject otherPObject = (PObject)obj;
			
		return targetOID.equals( otherPObject.getOID() );
	}
	
	public int invokeHashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( targetOID == null ) ? 0 : targetOID.hashCode() );
		return result;
	}

	public String invokeToString()
	{
		return "PObject [OID=" + targetOID + "]";
	}	
	
	private < T > Future< T > invokeGetFuture( PProcessHandle pProcessHandle )
	{
		return new LocalFuture< T >( pProcessHandle );
	}

	@SuppressFBWarnings(
		value="", 
		justification="" )
	private Object invokeNormally( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object retVal = null;
		
		// Create the invoker.
		PersistentObjectInvoker persistentObjectInvoker = createPersistentObjectInvoker();
		
		// This prevents us from having to handle the null case later.
		if ( args == null )
			args = new Object[] {};

		// Determine the synchronicity.
		ProxyMethod proxyMethod = persistentObjectInvoker.getProxyMethod( method );
		Synchronicity synchronicity = proxyMethod.synchronicity();
		
		// Pass the params to the invoker.
		persistentObjectInvoker.setSynchronicity( synchronicity );
		persistentObjectInvoker.setProxy( proxy );
		persistentObjectInvoker.setMethod( method );
		persistentObjectInvoker.setArgs( args );

		// Invoke sync or async.
		if ( synchronicity.equals( SYNCHRONOUS ) )
		{
			retVal = persistentObjectInvoker.call();
		}
		else if ( synchronicity.equals( ASYNCHRONOUS ) )
		{
			PEngine.getPEngine().getExecutorService().submit( (Runnable)persistentObjectInvoker );
		}
		else
			throw new IllegalStateException( "Unexpected value for synchronicity: " + synchronicity );
		
		return retVal;
	}
	
	protected abstract PersistentObjectInvoker createPersistentObjectInvoker();

	public PObjectOID getTargetOID()
	{
		return targetOID;
	}

	public MemoryModel getMemoryModel()
	{
		return memoryModel;
	}

	public void setTargetOID( PObjectOID targetOID )
	{
		this.targetOID = targetOID;
	}

	public void setMemoryModel( MemoryModel memoryModel )
	{
		this.memoryModel = memoryModel;
	}
}
