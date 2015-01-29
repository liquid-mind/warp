package ch.shaktipat.saraswati.internal.environment;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.manager.POwnableObjectManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.manager.PScheduledEventManager;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;

public abstract class PEnvironmentAbstract implements PEnvironment
{
	private static final long serialVersionUID = 1L;

	@Override
	public PProcessManager getPProcessManager()
	{
		return getPObjectManager( PProcessManager.class );
	}
	
	@Override
	public POwnableObjectManager getPOwnableObjectManager()
	{
		return getPObjectManager( POwnableObjectManager.class );
	}
	
	@Override
	public PEventQueueManager getPEventQueueManager()
	{
		return getPObjectManager( PEventQueueManager.class );
	}
	
	@Override
	public PEventTopicManager getPEventTopicManager()
	{
		return getPObjectManager( PEventTopicManager.class );
	}
	
	@Override
	public PScheduledEventManager getPScheduledEventManager()
	{
		return getPObjectManager( PScheduledEventManager.class );
	}
	
	@Override
	public PSharedObjectManager getPSharedObjectManager()
	{
		return getPObjectManager( PSharedObjectManager.class );
	}

	protected abstract < T > T getPObjectManager( Class< ? > pObjectManagerInterface );
}
