package ch.shaktipat.saraswati.internal.dynproxies;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class PersistentSharedObjectInvoker extends PersistentObjectInvoker
{
	private boolean invokerOfTargetObject;
	
	public PersistentSharedObjectInvoker( PSharedObjectOID targetOID, MemoryModel memoryModel )
	{
		this( targetOID, memoryModel, false );
	}
	
	public PersistentSharedObjectInvoker( PSharedObjectOID targetOID, MemoryModel memoryModel, boolean invokerOfTargetObject )
	{
		super( targetOID, memoryModel );
		this.invokerOfTargetObject = invokerOfTargetObject;
	}

	@Override
	protected Object invoke( PersistentObject persistentObject, Object targetObject, Method method, ProxyMethod proxyMethod, Object[] args ) throws Exception
	{
		Object retVal = null;
		PersistentSharedObject sharedObject = (PersistentSharedObject)persistentObject;
		
		PSharedObjectOID oldSharedObjectOID = setupThreadLocals( (PSharedObjectOID)sharedObject.getOID() );
			
		try
		{
			retVal = super.invoke( sharedObject, targetObject, method, proxyMethod, args );
		}
		finally
		{
			teardownThreadLocals( oldSharedObjectOID );
		}

		return retVal;
	}
	
	private PSharedObjectOID setupThreadLocals( PSharedObjectOID sharedObjectOID )
	{
		PSharedObjectOID oldSharedObjectOID = PersistentSharedObjectImpl.getCurrentObjectOID();
		PersistentSharedObjectImpl.setCurrentObjectOID( sharedObjectOID );
		
		return oldSharedObjectOID;
	}
	
	private void teardownThreadLocals( PSharedObjectOID sharedObjectOID )
	{
		PersistentSharedObjectImpl.setCurrentObjectOID( sharedObjectOID );
	}

	@Override
	public Object getTargetObject( PersistentObject persistentObject )
	{
		Object targetObject = super.getTargetObject( persistentObject );
		
		if ( invokerOfTargetObject )
		{
			PersistentSharedObject pSharedObject = (PersistentSharedObject)persistentObject;
			targetObject = pSharedObject.getWithoutProxy();
		}

		return targetObject;
	}

	@Override
	public ProxyMethod getProxyMethod( Method method )
	{
		ProxyMethod proxyMethod = null;
		
		// For the actual shared object we "force" a ProxyMethod definition.
		if ( invokerOfTargetObject )
			proxyMethod = new SharedObjectProxyMethodDefault();
		else
			proxyMethod = super.getProxyMethod( method );
		
		return proxyMethod;
	}
	
	@SuppressWarnings( "all" )
	public static class SharedObjectProxyMethodDefault implements ProxyMethod {
		@Override public Class< ? extends Annotation > annotationType() { return ProxyMethod.class; }
		@Override public Synchronicity synchronicity() { return Synchronicity.SYNCHRONOUS; }
		@Override public StateProtection stateProtection() { return null; }
		@Override public ExecutionProtection executionStateProtection() { return null; }
		@SuppressFBWarnings(
			value="PZLA_PREFER_ZERO_LENGTH_ARRAYS",
			justification="Default for validProcessStates is really null." )
		@Override public String[] validProcessStates() { return null; }
	};
	
	@Override
	public PSharedObjectOID getTargetOID()
	{
		return (PSharedObjectOID)super.getTargetOID();
	}
}
