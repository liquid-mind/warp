package ch.shaktipat.saraswati.internal.web.controller.peventtopic;

import java.util.HashSet;
import java.util.Set;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopic;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventTopicImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.event.Subscription;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.PEventTopicDetailModel;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.SubscriptionModel;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public class PEventTopicDetailController extends PEventTopicController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected PEventTopicDetailModel setupModel()
	{
		PEventTopicManager manager = PEnvironmentFactory.createLocal().getPEventTopicManager();
		PEventTopicOID pEventTopicOID = getPObjectOIDParameter();
		PEventTopic pEventTopic = manager.findByOID( pEventTopicOID );
		PProcessHandle owningProcess = pEventTopic.getOwningProcess();
		String command = getCommand();
		boolean commandSuccess = true;
		PEventTopicDetailModel model = null;

		if ( command.equals( DESTROY_COMMAND ) )
			pEventTopic.destroy();
		else if ( !command.isEmpty() )
			throw new RuntimeException( "Unexpected command: " + command );
		
		if ( command.equals( DESTROY_COMMAND ) )
			model = new PEventTopicDetailModel( commandSuccess );
		else
			model = new PEventTopicDetailModel(
			commandSuccess,
			pEventTopic.getOID(),
			pEventTopic.getName(),
			pEventTopic.getCreateDate(),
			getProcessOID( owningProcess ),
			getProcessName( owningProcess ),
			getSubscriptions( pEventTopicOID ) );
		
		return model;
	}
	
	private Set< SubscriptionModel > getSubscriptions( PEventTopicOID pEventTopicOID )
	{
		PersistentEventTopic eventTopicInternal = PersistentEventTopicImpl.getOtherProxy( pEventTopicOID );
		Set< Subscription > subscriptions = eventTopicInternal.getSubscriptions();
		Set< SubscriptionModel > subscriptionModels = new HashSet< SubscriptionModel >();
		
		for ( Subscription subscription : subscriptions )
		{
			PEventQueueOID targetEventQueueOID = subscription.getTargetEventQueueOID();
			PersistentEventQueue persistentEventQueue = PersistentEventQueueImpl.getOtherProxy( targetEventQueueOID );
			String targetEventQueueName = persistentEventQueue.getName();

			subscriptionModels.add( new SubscriptionModel( true, subscription.getSubscriptionOID(), subscription.getTargetEventQueueOID(),
				targetEventQueueName, subscription.getEventFilter().getFilterExpression(), subscription.isOneTimeOnly() ) );
		}

		return subscriptionModels;
	}
}
