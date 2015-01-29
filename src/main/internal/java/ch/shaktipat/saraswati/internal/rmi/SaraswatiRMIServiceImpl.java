package ch.shaktipat.saraswati.internal.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopicImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.manager.PObjectManager;
import ch.shaktipat.saraswati.pobject.manager.POwnableObjectManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.manager.PScheduledEventManager;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public class SaraswatiRMIServiceImpl extends UnicastRemoteObject implements SaraswatiRMIService
{
	private static final long serialVersionUID = 1L;

	private static final Map< String, PObjectManager > POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP;
	
	static
	{
		PEnvironment pEnvironment = PEnvironmentFactory.createLocal();
		
		// Note that PObjectManagerInvocationHandler is thread-safe, so we only need
		// one instance per manager type.
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP = new HashMap< String, PObjectManager >();
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( PProcessManager.class.getName(), pEnvironment.getPProcessManager() );
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( POwnableObjectManager.class.getName(), pEnvironment.getPOwnableObjectManager() );
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( PEventQueueManager.class.getName(), pEnvironment.getPEventQueueManager() );
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( PEventTopicManager.class.getName(), pEnvironment.getPEventTopicManager() );
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( PScheduledEventManager.class.getName(), pEnvironment.getPScheduledEventManager() );
		POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.put( PSharedObjectManager.class.getName(), pEnvironment.getPSharedObjectManager() );
	}

	protected SaraswatiRMIServiceImpl() throws RemoteException
	{
		super();
	}
	
	@Override
	public Object invokeOnPObjectManager( String className, String methodName, String[] paramTypeNames, Object[] args ) throws RemoteException
	{
		PObjectManager pObjectManager = POBJECTMANAGER_CLASS_NAME_TO_INSTANCE_MAP.get( className );
		
		return invoke( pObjectManager, className, paramTypeNames, methodName, args );
	}

	@Override
	public Object invokeOnPObject( PObjectOID pObjectOID, String className, String methodName, String[] paramTypeNames, Object[] args ) throws RemoteException
	{
		Object pObject = convertPObjectOIDToPObject( pObjectOID, className );
		
		return invoke( pObject, className, paramTypeNames, methodName, args );
	}
	
	private Object invoke( Object targetObject, String className, String[] paramTypeNames, String methodName, Object[] args ) throws RemoteException
	{
		Class< ? > pObjectManagerClass = JavaClassHelper.getClass( className );
		Class< ? >[] paramTypes = JavaClassHelper.getClasses( paramTypeNames );
		Method method = ClassWrapper.getDeclaredMethod( pObjectManagerClass, methodName, paramTypes );
		Object retVal = null;
		
		try
		{
			retVal = method.invoke( targetObject, args );
		}
		catch ( InvocationTargetException ite )
		{
			throw new SaraswatiRemoteException( ite.getTargetException() );
		}
		catch ( Throwable t )
		{
			throw new SaraswatiRemoteException( t );
		}
		
		return convertToPObjectOIDs( retVal );
	}

	@SuppressWarnings( "unchecked" )
	private Object convertToPObjectOIDs( Object sourceObject )
	{
		Object targetObject = sourceObject;
		
		if ( sourceObject != null )
		{
			if ( sourceObject instanceof PObject )
			{
				targetObject = convertPObjectToPObjectOID( (PObject)sourceObject );
			}
			else if ( sourceObject instanceof Collection )
			{
				Collection< ? > sourceObjectAsCollection = (Collection< ? >)sourceObject;
				Iterator< ? > iter = sourceObjectAsCollection.iterator();
				
				if ( iter.hasNext() && iter.next() instanceof PObject )
					targetObject = convertPObjectsToPObjectOIDs( (Collection< PObject >)sourceObjectAsCollection );
			}
		}
		
		return targetObject;
	}
	
	@SuppressWarnings( "unchecked" )
	private static Collection< PObjectOID > convertPObjectsToPObjectOIDs( Collection< PObject > pObjects )
	{
		Collection< PObjectOID > pObjectOIDs = (Collection< PObjectOID >)ClassWrapper.newInstance( pObjects.getClass() );
		
		for ( PObject pObject : pObjects )
			pObjectOIDs.add( convertPObjectToPObjectOID( pObject ) );
		
		return pObjectOIDs;
	}

	private static PObjectOID convertPObjectToPObjectOID( PObject pObject )
	{
		return pObject.getOID();
	}
	
	private static Object convertPObjectOIDToPObject( PObjectOID pObjectOID, String className )
	{
		Object pObject = null;
		
		if ( pObjectOID instanceof PProcessOID )
		{
			// Note that this is a really ugly workaround to allow for invocations through the PersistentProcess
			// interface (invocations through internal interfaces were originally not considered). This is
			// necessary, e.g., to support remote futures.
			// TODO The whole remote/local interface design is ill-concieved and needs to be replaced (I'm
			// fairly sure I said this somewhere else). Besides this problem there is also the issue of
			// how to handle serialization of local/remote handles.
			if ( className.equals( PersistentProcess.class.getName() ) )
				pObject = PersistentProcessImpl.getOtherProxy( (PProcessOID)pObjectOID );
			else
				pObject = PersistentProcessImpl.createPProcessHandleProxy( (PProcessOID)pObjectOID );
		}
		else if ( pObjectOID instanceof PEventQueueOID )
			pObject = PersistentEventQueueImpl.createPEventQueueProxy( (PEventQueueOID)pObjectOID );
		else if ( pObjectOID instanceof PEventTopicOID )
			pObject = PersistentEventTopicImpl.createPEventTopicProxy( (PEventTopicOID)pObjectOID );
		else if ( pObjectOID instanceof PScheduledEventOID )
			pObject = PersistentScheduledEventImpl.createPScheduledEventProxy( (PScheduledEventOID)pObjectOID );
		else if ( pObjectOID instanceof PSharedObjectOID )
			pObject = PersistentSharedObjectImpl.createPSharedObjectHandleProxy( (PSharedObjectOID)pObjectOID );
		else
			throw new IllegalStateException( "Unexpected type for pObjectOID: " + pObjectOID.getClass().getName() );
			
		return pObject;
	}
}
