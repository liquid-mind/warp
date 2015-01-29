package ch.shaktipat.saraswati.internal.scheduler;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class ScheduleEventCommand extends SchedulerCommand
{
	private PEventQueueOID targetEventQueueOID;
	private TimeSpecification timeSpecification;
	
	public ScheduleEventCommand( PersistentEventScheduler scheduler, PEventQueueOID targetEventQueueOID, TimeSpecification timeSpecification )
	{
		super( scheduler );
		this.targetEventQueueOID = targetEventQueueOID;
		this.timeSpecification = timeSpecification;
	}

	public PEventQueueOID getTargetEventQueueOID()
	{
		return targetEventQueueOID;
	}

	public TimeSpecification getTimeSpecification()
	{
		return timeSpecification;
	}

	@Override
	public void executeInternal()
	{
		Event event = new TimeoutEvent();
		PScheduledEventOID pScheduledEventOID = PersistentScheduledEventImpl.create( null, event, timeSpecification, targetEventQueueOID ).getOID();
		setCommandResult( pScheduledEventOID );
	}
}
