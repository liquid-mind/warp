package ch.shaktipat.saraswati.internal.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueue;
import ch.shaktipat.saraswati.internal.pobject.PersistentEventQueueImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEvent;
import ch.shaktipat.saraswati.internal.pobject.PersistentScheduledEventImpl;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.pool.PersistentObjectNotFoundExeception;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class PersistentEventScheduler implements Runnable
{
	private static Logger logger = Logger.getLogger( PersistentEventScheduler.class.getName() );

	private Queue< SchedulerCommand > commandQueue;
	private OmniObjectPool omniObjectPool;
	private Thread schedulerThread;
	
	public PersistentEventScheduler( OmniObjectPool omniObjectPool )
	{
		commandQueue = new LinkedBlockingQueue< SchedulerCommand >();
		this.omniObjectPool = omniObjectPool;
		schedulerThread = new Thread( this, PersistentEventScheduler.class.getSimpleName() );
	}
	
	public void start( OmniObjectPool omniObjectPool )
	{
		schedulerThread.start();
	}
	
	public void stop()
	{
		new ShutdownCommand( this ).sendCommandSync();
		ThreadWrapper.join( schedulerThread );
	}
	
	@Override
	public void run()
	{
		try
		{
			doScheduling();
		}
		catch ( Throwable t )
		{
			logger.log(  Level.SEVERE, "Unexpected exception caught by " + PersistentEventScheduler.class.getSimpleName(), t );
		}
	}
	
	private void doScheduling()
	{
		while ( true )
		{
			SchedulerCommand command = commandQueue.poll();
			
			if ( command != null )
			{
				command.execute();

				if ( command instanceof ShutdownCommand )
					break;
			}
			
			PersistentScheduledEvent nextEvent = getNextPersistentScheduledEvent();
			
			if ( nextEvent != null )
			{
				if ( awaitDeadline( nextEvent ) )
					deliverEvent( nextEvent );
			}
			else
			{
				awaitCommand();
			}
		}
	}
	
	private PersistentScheduledEvent getNextPersistentScheduledEvent()
	{
		List< PScheduledEventOID > events = omniObjectPool.findByEarliestEvent();
		PersistentScheduledEvent event = null;
		
		if ( events.size() > 0 )
		{
			PScheduledEventOID eventOID = events.get( 0 );
			event = PersistentScheduledEventImpl.getOtherProxy( eventOID );
		}
		
		return event;
	}
	
	private boolean awaitDeadline( PersistentScheduledEvent event )
	{
		boolean deadlineReached = false;
		
		while ( !Thread.interrupted() )
		{
			LockSupport.parkUntil( event.getDeadline().getTime() );

			if ( event.getDeadline().before( new Date() ) )
			{
				deadlineReached = true;
				break;
			}
		}
		
		return deadlineReached;
	}
	
	private void deliverEvent( PersistentScheduledEvent event )
	{
		try
		{
			PersistentEventQueue eventQueue = PersistentEventQueueImpl.getOtherProxy( event.getEventQueueOID() );
			eventQueue.handleTimeoutEvent( event.getOID() );
			event.destroy();
		}
		catch( PersistentObjectNotFoundExeception e )
		{}
	}
	
	private void awaitCommand()
	{
		while ( !Thread.interrupted() )
		{
			LockSupport.park();
		}
	}
	
	public PScheduledEventOID scheduleEvent( PEventQueueOID targetEventQueueOID, TimeSpecification timeSpecification )
	{
		return (PScheduledEventOID)new ScheduleEventCommand( this, targetEventQueueOID, timeSpecification ).sendCommandSync();
	}
	
	public void unscheduleEvent( PScheduledEventOID pScheduledEventOID )
	{
		new UnscheduleEventCommand( this, pScheduledEventOID ).sendCommandAsync();
	}

	public Queue< SchedulerCommand > getCommandQueue()
	{
		return commandQueue;
	}

	public Thread getSchedulerThread()
	{
		return schedulerThread;
	}
}
