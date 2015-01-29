package ch.shaktipat.exwrapper.java.lang;

public class ThreadWrapper
{
	public static void sleep( long millis )
	{
		try
		{
			Thread.sleep( millis );
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
	
	public static void join( Thread thread )
	{
		try
		{
			thread.join();
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
}
