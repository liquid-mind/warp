package ch.shaktipat.saraswati.environment;

import java.io.Serializable;

import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.manager.POwnableObjectManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.manager.PScheduledEventManager;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;

public interface PEnvironment extends Serializable
{
	public PProcessManager getPProcessManager();
	public POwnableObjectManager getPOwnableObjectManager();
	public PEventQueueManager getPEventQueueManager();
	public PEventTopicManager getPEventTopicManager();
	public PScheduledEventManager getPScheduledEventManager();
	public PSharedObjectManager getPSharedObjectManager();
}
