package ch.shaktipat.saraswati.internal.web.controller.peventqueue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.event.EventListener;
import ch.shaktipat.saraswati.internal.web.model.peventqueue.PEventQueueDetailModel;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public class PEventQueueDetailController extends PEventQueueController
{
	private enum ExtendedDataItems
	{
		LISTENING_PROCESS,
		LISTENING_FILTER,
		LISTENING_TIMEOUT,
		QUEUED_EVENT_TYPES
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	protected PEventQueueDetailModel setupModel()
	{
		PEventQueueManager manager = PEnvironmentFactory.createLocal().getPEventQueueManager();
		PEventQueueOID pEventQueueOID = getPObjectOIDParameter();
		PEventQueue pEventQueue = manager.findByOID( pEventQueueOID );
		PProcessHandle owningProcess = pEventQueue.getOwningProcess();
		Map< ExtendedDataItems, Object > exData = getExtendedDataItems( manager, pEventQueueOID );
		String command = getCommand();
		boolean commandSuccess = true;
		PEventQueueDetailModel model = null;

		if ( command.equals( DESTROY_COMMAND ) )
			pEventQueue.destroy();
		else if ( !command.isEmpty() )
			throw new RuntimeException( "Unexpected command: " + command );
		
		if ( command.equals( DESTROY_COMMAND ) )
			model = new PEventQueueDetailModel( commandSuccess );
		else
			model = new PEventQueueDetailModel(
				true,
				pEventQueue.getOID(),
				pEventQueue.getName(),
				pEventQueue.getCreateDate(),
				getProcessOID( owningProcess ),
				getProcessName( owningProcess ),
				getProcessOID( (PProcessHandle)exData.get( ExtendedDataItems.LISTENING_PROCESS ) ),
				getProcessName( (PProcessHandle)exData.get( ExtendedDataItems.LISTENING_PROCESS ) ),
				(String)exData.get( ExtendedDataItems.LISTENING_FILTER ),
				(Date)exData.get( ExtendedDataItems.LISTENING_TIMEOUT ),
				(List< Class< ? > >)exData.get( ExtendedDataItems.QUEUED_EVENT_TYPES ) );
		
		return model;
	}
	
	private Map< ExtendedDataItems, Object > getExtendedDataItems( PEventQueueManager manager, PEventQueueOID pEventQueueOID )
	{
		PProcessManager processManager = PEnvironmentFactory.createLocal().getPProcessManager();
		PersistentEventQueue eventQueueInternal = PersistentEventQueueImpl.getOtherProxy( pEventQueueOID );
		List< Class< ? > > queuedEventTypes = eventQueueInternal.getQueuedEventTypes();
		EventListener listener = eventQueueInternal.getListener();
		PProcessHandle listeningProcess = null;
		PersistentScheduledEvent scheduledEvent = null;
		String listeningFilter = null;
		
		if ( listener != null )
		{
			listeningProcess = processManager.getHandle( listener.getTargetProcessOID() );
			scheduledEvent = PersistentScheduledEventImpl.getOtherProxy( listener.getScheduledTimeoutEventOID() );
			
			if ( listener.getEventFilter() != null )
				listeningFilter = listener.getEventFilter().getFilterExpression();
		}

		Date listeningTimeout = null;
		
		if ( scheduledEvent != null )
			listeningTimeout = scheduledEvent.getDeadline();

		Map< ExtendedDataItems, Object > exData = new HashMap< ExtendedDataItems, Object >();
		exData.put( ExtendedDataItems.LISTENING_PROCESS, listeningProcess );
		exData.put( ExtendedDataItems.LISTENING_FILTER, listeningFilter );
		exData.put( ExtendedDataItems.LISTENING_TIMEOUT, listeningTimeout );
		exData.put( ExtendedDataItems.QUEUED_EVENT_TYPES, queuedEventTypes );
		
		return exData;
	}
}
