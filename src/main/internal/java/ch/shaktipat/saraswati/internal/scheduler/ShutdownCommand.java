package ch.shaktipat.saraswati.internal.scheduler;


public class ShutdownCommand extends SchedulerCommand
{
	public ShutdownCommand( PersistentEventScheduler scheduler )
	{
		super( scheduler );
	}

	@Override
	protected void executeInternal() {}
}
