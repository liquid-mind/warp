package ch.shaktipat.saraswati.common;

import java.util.Date;


public class NanoSpecification extends TimeSpecification
{
	private static final long serialVersionUID = 1L;

	private long nanos;

	public NanoSpecification( long nanos )
	{
		super();
		this.nanos = nanos;
	}

	public long getNanos()
	{
		return nanos;
	}

	public void setNanos( long nanos )
	{
		this.nanos = nanos;
	}

	@Override
	public Date calcDeadline()
	{
		long initMillis = new Date().getTime();
		Date deadline = new Date( nanos / 1000000 + initMillis );
		
		return deadline;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)( nanos ^ ( nanos >>> 32 ) );
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
		NanoSpecification other = (NanoSpecification)obj;
		if ( nanos != other.nanos )
			return false;
		return true;
	}
}
