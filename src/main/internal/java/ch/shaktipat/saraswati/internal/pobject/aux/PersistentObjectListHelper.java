package ch.shaktipat.saraswati.internal.pobject.aux;

import java.util.ArrayList;
import java.util.List;

import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopic;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopicImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentSharedObjectImpl;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PObject;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PScheduledEvent;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public class PersistentObjectListHelper
{
	@SuppressWarnings( "unchecked" )
	public static < T extends PersistentObject, V extends PObjectOID > List< T > createInternalProxyList( Class< T > targetInterface, List< V > oidList )
	{
		List< T > proxyList = new ArrayList< T >();
		
		for ( PObjectOID oid : oidList )
			proxyList.add( (T)getInternalProxy( targetInterface, oid ) );
		
		return proxyList;
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends PObject, V extends PObjectOID > List< T > createExternalProxyList( Class< T > targetInterface, List< V > oidList )
	{
		List< T > proxyList = new ArrayList< T >();
		
		for ( PObjectOID oid : oidList )
			proxyList.add( (T)getExternalProxy( targetInterface, oid ) );
		
		return proxyList;
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T > T getInternalProxy( Class< ? > targetInterface, PObjectOID oid )
	{
		T proxy = null;
		
		if ( targetInterface.equals( PersistentProcess.class ) )
			proxy = (T)PersistentProcessImpl.getOtherProxy( (PProcessOID)oid );
		else if ( targetInterface.equals( PersistentSharedObject.class ) )
			proxy = (T)PersistentSharedObjectImpl.getOtherProxy( (PSharedObjectOID)oid );
		else if ( targetInterface.equals( PersistentEventQueue.class ) )
			proxy = (T)PersistentEventQueueImpl.getOtherProxy( (PEventQueueOID)oid );
		else if ( targetInterface.equals( PersistentEventTopic.class ) )
			proxy = (T)PersistentEventTopicImpl.getOtherProxy( (PEventTopicOID)oid );
		else if ( targetInterface.equals( PersistentScheduledEvent.class ) )
			proxy = (T)PersistentScheduledEventImpl.getOtherProxy( (PScheduledEventOID)oid );
		else
			throw new IllegalStateException( "Unexpected type for targetInterface: " + targetInterface );
		
		return proxy;
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T > T getExternalProxy( Class< ? > targetInterface, PObjectOID oid )
	{
		T proxy = null;

		if ( targetInterface.equals( PProcessHandle.class ) )
			proxy = (T)PersistentProcessImpl.createPProcessHandleProxy( (PProcessOID)oid );
		else if ( targetInterface.equals( PSharedObjectHandle.class ) )
			proxy = (T)PersistentSharedObjectImpl.createPSharedObjectHandleProxy( (PSharedObjectOID)oid );
		else if ( targetInterface.equals( PEventQueue.class ) )
			proxy = (T)PersistentEventQueueImpl.createPEventQueueProxy( (PEventQueueOID)oid );
		else if ( targetInterface.equals( PEventTopic.class ) )
			proxy = (T)PersistentEventTopicImpl.createPEventTopicProxy( (PEventTopicOID)oid );
		else if ( targetInterface.equals( PScheduledEvent.class ) )
			proxy = (T)PersistentScheduledEventImpl.createPScheduledEventProxy( (PScheduledEventOID)oid );
		else if ( targetInterface.equals( POwnableObject.class ) )
		{
			if ( oid instanceof PProcessOID )
				proxy = (T)PersistentProcessImpl.createPProcessHandleProxy( (PProcessOID)oid );
			else if ( oid instanceof PSharedObjectOID )
				proxy = (T)PersistentSharedObjectImpl.createPSharedObjectHandleProxy( (PSharedObjectOID)oid );
			else if ( oid instanceof PEventQueueOID )
				proxy = (T)PersistentEventQueueImpl.createPEventQueueProxy( (PEventQueueOID)oid );
			else if ( oid instanceof PEventTopicOID )
				proxy = (T)PersistentEventTopicImpl.createPEventTopicProxy( (PEventTopicOID)oid );
			else if ( oid instanceof PScheduledEventOID )
				proxy = (T)PersistentScheduledEventImpl.createPScheduledEventProxy( (PScheduledEventOID)oid );
		}
		else
			throw new IllegalStateException( "Unexpected type for targetInterface: " + targetInterface );
		
		return proxy;
	}
}
