package ch.shaktipat.saraswati.internal.dynproxies;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopicImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentOwnableObjectImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.manager.POwnableObjectManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.manager.PScheduledEventManager;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;

public class PObjectManagerInvocationHandler implements InvocationHandler, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final Map< Class< ? >, Class< ? > > POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP;
	
	static
	{
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP = new HashMap< Class< ? >, Class< ? > >();
		
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( PProcessManager.class, PersistentProcessImpl.class );
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( POwnableObjectManager.class, PersistentOwnableObjectImpl.class );
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( PEventQueueManager.class, PersistentEventQueueImpl.class );
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( PEventTopicManager.class, PersistentEventTopicImpl.class );
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( PScheduledEventManager.class, PersistentScheduledEventImpl.class );
		POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.put( PSharedObjectManager.class, PersistentSharedObjectImpl.class );
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		Class< ? > targetClass = POBJECTMANAGER_CLASS_TO_POBJECT_CLASS_MAP.get( method.getDeclaringClass() );
		Method targetMethod = ClassWrapper.getMethod( targetClass, method.getName(), method.getParameterTypes() );

		Object retVal = null;
		
		try
		{
			retVal = targetMethod.invoke( null, args );
		}
		catch ( InvocationTargetException ite )
		{
			throw ite.getTargetException();
		}
		
		return retVal;
	}
}
