package ch.shaktipat.exwrapper.java.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.exwrapper.java.lang.InterruptedExceptionWrapper;

public class ExecutorServiceWrapper
{
	public static boolean awaitTermination( ExecutorService executorService, long timeout, TimeUnit unit )
	{
		try
		{
			return executorService.awaitTermination( timeout, unit );
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
}
