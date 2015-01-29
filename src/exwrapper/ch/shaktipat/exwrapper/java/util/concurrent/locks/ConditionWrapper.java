package ch.shaktipat.exwrapper.java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import ch.shaktipat.exwrapper.java.lang.InterruptedExceptionWrapper;

public class ConditionWrapper
{
	public static void await( Condition condition )
	{
		try
		{
			condition.await();
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
	
	public static void await( Condition condition, long time, TimeUnit unit )
	{
		try
		{
			condition.await( time, unit );
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
	
	public static void await( Condition condition, Date deadline )
	{
		try
		{
			condition.awaitUntil( deadline );
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
}
