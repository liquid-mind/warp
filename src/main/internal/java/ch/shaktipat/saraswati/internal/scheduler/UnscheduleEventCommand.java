package ch.shaktipat.saraswati.internal.scheduler;

import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pool.PersistentObjectNotFoundExeception;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class UnscheduleEventCommand extends SchedulerCommand
{
	private PScheduledEventOID pScheduledEventOID;
	
	public UnscheduleEventCommand( PersistentEventScheduler scheduler, PScheduledEventOID pScheduledEventOID )
	{
		super( scheduler );
		this.pScheduledEventOID = pScheduledEventOID;
	}

	public PScheduledEventOID getpScheduledEventOID()
	{
		return pScheduledEventOID;
	}

	@Override
	protected void executeInternal()
	{
		// If the scheduled event was already delivered before it 
		// could be unscheduled then the object pool will throw
		// a PersistentObjectNotFoundExeception, which we can ignore.
		try
		{
			PersistentScheduledEvent pScheduledEvent = PersistentScheduledEventImpl.getOtherProxy( pScheduledEventOID );
			pScheduledEvent.destroy();
		}
		catch( PersistentObjectNotFoundExeception e )
		{}
	}
}
