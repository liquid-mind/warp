package ch.shaktipat.exwrapper.java.lang;

public class ProcessWrapper
{
	public static int waitFor( Process process )
	{
		try
		{
			return process.waitFor();
		}
		catch ( InterruptedException e )
		{
			throw new InterruptedExceptionWrapper( e );
		}
	}
}
