package ch.shaktipat.saraswati.internal.rmi;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.rmi.NamingWrapper;
import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PScheduledEvent;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public class PObjectManagerRMIClientInvocationHandler implements InvocationHandler, Serializable
{
	private static final long serialVersionUID = 1L;

	private static Map< Class< ? >, Class< ? > > POBJECT_OID_TO_POBJECT_MAP;
	
	static
	{
		POBJECT_OID_TO_POBJECT_MAP = new HashMap< Class< ? >, Class< ? > >();
		POBJECT_OID_TO_POBJECT_MAP.put( PProcessOID.class, PProcessHandle.class );
		POBJECT_OID_TO_POBJECT_MAP.put( PEventQueueOID.class, PEventQueue.class );
		POBJECT_OID_TO_POBJECT_MAP.put( PEventTopicOID.class, PEventTopic.class );
		POBJECT_OID_TO_POBJECT_MAP.put( PScheduledEventOID.class, PScheduledEvent.class );
		POBJECT_OID_TO_POBJECT_MAP.put( PSharedObjectOID.class, PSharedObjectHandle.class );
	}

	private String hostName;
	private int port;
	private SaraswatiRMIService saraswatiRMIService;

	public PObjectManagerRMIClientInvocationHandler( String hostName, int port )
	{
		super();
		this.hostName = hostName;
		this.port = port;

		// TODO Optimize the creation of SaraswatiRMIService so that there is only one per
		// thread (also effects PObjectRMIClientInvocationHandler).
		String saraswatiRMIServiceName = "//" + hostName + ":" + port + "/" + SaraswatiRMIService.class.getName();
		saraswatiRMIService = (SaraswatiRMIService)NamingWrapper.lookup( saraswatiRMIServiceName );
	}
	
	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		String className = method.getDeclaringClass().getName();
		String[] paramTypeNames = JavaClassHelper.getClassNames( method.getParameterTypes() );
		Object retVal = null;
		
		try
		{
			retVal = saraswatiRMIService.invokeOnPObjectManager( className, method.getName(), paramTypeNames, args );
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
		
		return convertToPObjects( retVal );
	}
	
	// TODO Converting PObject proxies to PObjectOIDs and back again is not a good way to
	// handle passing PObject references from client to server and vice versa. In particular,
	// it is possible for a PObjectOID to be ambiguous; does it represent a proxy or actually
	// a PObjectOID itself (could happen, e.g., if PSharedObject returned a PObjectOID)?
	// Also, any composite objects of the params/return values are not handled with the current
	// paradigm.
	// It would be better to introduce special handling for PObject proxy instances into the
	// RMI serialization/deserialization. Will require some engineering, so am leaving for now...
	@SuppressWarnings( "unchecked" )
	private Object convertToPObjects( Object sourceObject )
	{
		Object targetObject = sourceObject;
		
		if ( sourceObject != null )
		{
			if ( sourceObject instanceof PObjectOID )
			{
				targetObject = convertPObjectOIDToPObject( (PObjectOID)sourceObject );
			}
			else if ( sourceObject instanceof Collection )
			{
				Collection< ? > sourceObjectAsCollection = (Collection< ? >)sourceObject;
				Iterator< ? > iter = sourceObjectAsCollection.iterator();
				
				if ( iter.hasNext() && iter.next() instanceof PObjectOID )
					targetObject = convertPObjectOIDsToPObjects( (Collection< PObjectOID >)sourceObjectAsCollection );
			}
		}
		
		return targetObject;
	}
	
	@SuppressWarnings( "unchecked" )
	private Collection< PObject > convertPObjectOIDsToPObjects( Collection< PObjectOID > pObjectOIDs )
	{
		Collection< PObject > pObjects = (Collection< PObject >)ClassWrapper.newInstance( pObjectOIDs.getClass() );
		
		for ( PObjectOID pObjectOID : pObjectOIDs )
			pObjects.add( convertPObjectOIDToPObject( pObjectOID ) );
		
		return pObjects;
	}
	
	private PObject convertPObjectOIDToPObject( PObjectOID pObjectOID )
	{
		Class< ? > pObjectInterface = POBJECT_OID_TO_POBJECT_MAP.get( pObjectOID.getClass() );
		PObject pObject = (PObject)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class< ? >[] { pObjectInterface }, new PObjectRMIClientInvocationHandler( pObjectOID, hostName, port ) );

		return pObject;
	}
}
