package ch.shaktipat.saraswati.common;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class TimeAndUnitSpecification extends TimeSpecification
{
	private static final long serialVersionUID = 1L;
	
	private long time;
	private TimeUnit timeUnit;
	
	public TimeAndUnitSpecification( long time, TimeUnit timeUnit )
	{
		super();
		this.time = time;
		this.timeUnit = timeUnit;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime( long time )
	{
		this.time = time;
	}

	public TimeUnit getTimeUnit()
	{
		return timeUnit;
	}

	public void setTimeUnit( TimeUnit timeUnit )
	{
		this.timeUnit = timeUnit;
	}

	@Override
	public Date calcDeadline()
	{
		long initMillis = new Date().getTime();
		Date deadline = new Date( TimeUnit.MILLISECONDS.convert( time, timeUnit ) + initMillis );
		
		return deadline;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)( time ^ ( time >>> 32 ) );
		result = prime * result + ( ( timeUnit == null ) ? 0 : timeUnit.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		TimeAndUnitSpecification other = (TimeAndUnitSpecification)obj;
		if ( time != other.time )
			return false;
		if ( timeUnit != other.timeUnit )
			return false;
		return true;
	}
}
