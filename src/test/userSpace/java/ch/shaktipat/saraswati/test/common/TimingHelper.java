package ch.shaktipat.saraswati.test.common;

import java.util.Calendar;
import java.util.Date;

public class TimingHelper
{
	public static Date getDeadline( long millisFromNow )
	{
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime( new Date() );
	    calendar.add( Calendar.MILLISECOND, 1000 );
	    Date deadline = calendar.getTime();
	    
	    return deadline;
	}

	public static void sleep( long millis )
	{
		try
		{
			Thread.sleep( millis );
		}
		catch ( InterruptedException e )
		{
		}
	}
}
