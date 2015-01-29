package ch.shaktipat.saraswati.internal.pobject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.ConstructorWrapper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessCommon;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PScheduledEvent;
import ch.shaktipat.saraswati.pobject.PSharedObject;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public class PObjectOIDFactory
{
	private static final Map< Class< ? >, Class< ? > > POBJECT_TYPE_TO_OID_TYPE_MAP = new HashMap< Class< ? >, Class< ? > >();
	
	static
	{
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentObjectImpl.class, PObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentObject.class, PObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PObject.class, PObjectOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentOwnableObjectImpl.class, POwnableObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentOwnableObject.class, POwnableObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( POwnableObject.class, POwnableObjectOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentProcessImpl.class, PProcessOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentProcess.class, PProcessOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PProcess.class, PProcessOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PProcessHandle.class, PProcessOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PProcessCommon.class, PProcessOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentEventQueueImpl.class, PEventQueueOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentEventQueue.class, PEventQueueOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PEventQueue.class, PEventQueueOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentEventTopicImpl.class, PEventTopicOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentEventTopic.class, PEventTopicOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PEventTopic.class, PEventTopicOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentScheduledEventImpl.class, PScheduledEventOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentScheduledEvent.class, PScheduledEventOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PScheduledEvent.class, PScheduledEventOID.class );

		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentSharedObjectImpl.class, PSharedObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PersistentSharedObject.class, PSharedObjectOID.class );
		POBJECT_TYPE_TO_OID_TYPE_MAP.put( PSharedObject.class, PSharedObjectOID.class );
	}
	
	public static PObjectOID create( Class< ? > pObjectClass )
	{
		Class< ? > pObjectOIDClass = POBJECT_TYPE_TO_OID_TYPE_MAP.get( pObjectClass );
		PObjectOID pObjectOID = (PObjectOID)ClassWrapper.newInstance( pObjectOIDClass );
		
		return pObjectOID;
	}
	
	public static PObjectOID create( Class< ? > pObjectClass, UUID uuid )
	{
		Class< ? > pObjectOIDClass = POBJECT_TYPE_TO_OID_TYPE_MAP.get( pObjectClass );
		Constructor< ? > pObjectOIDConstructor = ClassWrapper.getDeclaredConstructor( pObjectOIDClass, UUID.class );
		PObjectOID pObjectOID = (PObjectOID)ConstructorWrapper.newInstance( pObjectOIDConstructor, uuid );
		
		return pObjectOID;
	}
}
