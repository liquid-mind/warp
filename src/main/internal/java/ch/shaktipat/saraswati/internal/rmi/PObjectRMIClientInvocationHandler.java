package ch.shaktipat.saraswati.internal.rmi;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.concurrent.Future;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.rmi.NamingWrapper;
import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.RemoteFuture;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PObjectRMIClientInvocationHandler implements InvocationHandler, Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Method GET_FUTURE_METHOD = ClassWrapper.getDeclaredMethod( PProcessHandle.class, FieldAndMethodConstants.GET_FUTURE_METHOD );
	
	private PObjectOID targetOID;
	private String hostName;
	private int port;
	private SaraswatiRMIService saraswatiRMIService;
	

	public PObjectRMIClientInvocationHandler( PObjectOID targetOID, String hostName, int port )
	{
		super();
		this.targetOID = targetOID;
		this.hostName = hostName;
		this.port = port;

		String saraswatiRMIServiceName = "//" + hostName + ":" + port + "/" + SaraswatiRMIService.class.getName();
		saraswatiRMIService = (SaraswatiRMIService)NamingWrapper.lookup( saraswatiRMIServiceName );
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Object retVal = null;

		if ( method.equals( GET_FUTURE_METHOD ) )
			retVal = invokeGetFuture( (PProcessHandle)proxy );
		else
			retVal = invokeNormally( proxy, method, args );
		
		return retVal;
	}
	
	private < T > Future< T > invokeGetFuture( PProcessHandle pProcessHandle )
	{
		return new RemoteFuture< T >( pProcessHandle, hostName, port );
	}

	private Object invokeNormally( Object proxy, Method method, Object[] args ) throws Throwable
	{
		String className = method.getDeclaringClass().getName();
		String[] paramTypeNames = JavaClassHelper.getClassNames( method.getParameterTypes() );
		Object retVal = null;
		
		try
		{
			retVal = saraswatiRMIService.invokeOnPObject( targetOID, className, method.getName(), paramTypeNames, args );
		}
		catch ( RemoteException e )
		{
			if ( e instanceof SaraswatiRemoteException )
			{
				SaraswatiRemoteException sre = (SaraswatiRemoteException)e;
				throw sre.getCause();
			}
			else
			{
				// TODO Introduce exception type for this case.
				throw new RuntimeException( e );
			}
		}
		
		return retVal;
	}
}
