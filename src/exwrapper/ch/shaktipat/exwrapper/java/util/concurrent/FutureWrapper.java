package ch.shaktipat.exwrapper.java.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.shaktipat.exwrapper.java.lang.InterruptedExceptionWrapper;

public class FutureWrapper
{
	public static Object get( Future< ? > future )
	{
		try
		{
			return future.get();
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
		catch ( ExecutionException e )
		{
			throw new ExecutionExceptionWrapper( e );
		}
	}
}
