package ch.shaktipat.saraswati.internal.dynproxies;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

// TODO Subclass PersistentObjectInvocationHandler for all other persistent object
// type, override createPersistentObjectInvoker() and get rid of invokerClass arg.
public class PersistentSharedObjectInvocationHandler extends PersistentObjectInvocationHandler
{
	private static final long serialVersionUID = 1L;

	private static final Method GET_METHOD = ClassWrapper.getDeclaredMethod( PSharedObjectHandle.class, FieldAndMethodConstants.GET_METHOD );
	
	private boolean invokerOfTargetObject;
	
	public PersistentSharedObjectInvocationHandler( boolean invokerOfTargetObject )
	{
		super();
		this.invokerOfTargetObject = invokerOfTargetObject;
	}
	
	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object retVal = null;
		
		if ( method.equals( GET_METHOD ) )
			retVal = invokeGet();
		else
			retVal = super.invoke( proxy, method, args );
		
		return retVal;
	}
	
	private Object invokeGet()
	{
		PersistentSharedObject pSharedObject = PersistentSharedObjectImpl.getOtherProxy( getTargetOID() );
		Object sharedObject = pSharedObject.getWithoutProxy();
		PersistentObjectInvocationHandler handler = new PersistentSharedObjectInvocationHandler( true );
		handler.setTargetOID( getTargetOID() );
		handler.setMemoryModel( getMemoryModel() );
		Object proxy = Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), sharedObject.getClass().getInterfaces(), handler );
		
		return proxy;
	}

	@Override
	protected PersistentObjectInvoker createPersistentObjectInvoker()
	{
		return new PersistentSharedObjectInvoker( getTargetOID(), getMemoryModel(), invokerOfTargetObject );
	}

	@Override
	public PSharedObjectOID getTargetOID()
	{
		return (PSharedObjectOID)super.getTargetOID();
	}
}
