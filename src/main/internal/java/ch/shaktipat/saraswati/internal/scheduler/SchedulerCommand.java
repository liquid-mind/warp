package ch.shaktipat.saraswati.internal.scheduler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.shaktipat.saraswati.internal.engine.PEngine;

public abstract class SchedulerCommand implements Runnable
{
	private PersistentEventScheduler scheduler;
	private WriteLock commandLock;
	private Condition commandCompletedCondition;
	private Object commandResult;

	public SchedulerCommand( PersistentEventScheduler scheduler )
	{
		this.scheduler = scheduler;
		commandLock = new ReentrantReadWriteLock().writeLock();
		commandCompletedCondition = commandLock.newCondition();
	}
	
	public Object sendCommandSync()
	{
		commandLock.lock();
		
		try
		{
			scheduler.getCommandQueue().add( this );
			scheduler.getSchedulerThread().interrupt();
			commandCompletedCondition.awaitUninterruptibly();
		}
		finally
		{
			commandLock.unlock();
		}
		
		return commandResult;
	}
	
	public void signalCommandCompleted()
	{
		commandLock.lock();
		
		try
		{
			commandCompletedCondition.signal();
		}
		finally
		{
			commandLock.unlock();
		}
	}
	
	public void sendCommandAsync()
	{
		PEngine.getPEngine().getExecutorService().execute( this );
	}

	@Override
	public void run()
	{
		sendCommandSync();
	}
	
	public void execute()
	{
		executeInternal();
		signalCommandCompleted();
	}
	
	protected abstract void executeInternal();

	public Object getCommandResult()
	{
		return commandResult;
	}

	public void setCommandResult( Object commandResult )
	{
		this.commandResult = commandResult;
	}
}
