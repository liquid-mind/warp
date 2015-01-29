package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.pobject.PProcessHandle;

public abstract class PersistentProcessFuture< T > implements Future< T >, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private PProcessHandle pProcessHandle;
	
	public PersistentProcessFuture( PProcessHandle pProcessHandle )
	{
		super();
		this.pProcessHandle = pProcessHandle;
	}

	@Override
	public boolean cancel( boolean mayInterruptIfRunning )
	{
		pProcessHandle.cancel();
		
		// TODO: is there a better way to handle this?
		return true;
	}

	@Override
	public boolean isCancelled()
	{
		// TODO implement this (need to add new substates to TERMINATED_STATE:
		// CANCELLED_STATE, RETURNED_STATE and EXCEPTION_STATE).
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDone()
	{
		return pProcessHandle.isInState( PersistentProcessStateMachine.TERMINATED_STATE );
	}

	@Override
	public T get() throws InterruptedException, ExecutionException
	{
		pProcessHandle.join();
		return getWithoutWait();
	}

	@Override
	public T get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
	{
		pProcessHandle.join( timeout, unit );
		return getWithoutWait();
	}

	@SuppressWarnings( "unchecked" )
	public T getWithoutWait() throws InterruptedException, ExecutionException
	{
		PProcessExecutionSegmentResult result = getPersistentProcess().getpProcessExecutionSegmentResult();
		
		if ( result.getException() != null )
			throw new ExecutionException( result.getException() );
		
		return (T)result.getReturnValue();
	}
	
	protected PProcessHandle getPProcessHandle()
	{
		return pProcessHandle;
	}
	
	protected abstract PersistentProcess getPersistentProcess();
}
