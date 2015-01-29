package ch.shaktipat.saraswati.common;

import java.util.Date;

import ch.shaktipat.saraswati.internal.common.SerializationHelper;


public class DeadlineSpecification extends TimeSpecification
{
	private static final long serialVersionUID = 1L;

	private Date deadline;

	public DeadlineSpecification( Date deadline )
	{
		super();
		this.deadline = (Date)SerializationHelper.cloneObject( deadline );
	}

	public Date getDeadline()
	{
		return deadline;
	}

	public void setDeadline( Date deadline )
	{
		this.deadline = (Date)SerializationHelper.cloneObject( deadline );
	}

	@Override
	public Date calcDeadline()
	{
		return deadline;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( deadline == null ) ? 0 : deadline.hashCode() );
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
		DeadlineSpecification other = (DeadlineSpecification)obj;
		if ( deadline == null )
		{
			if ( other.deadline != null )
				return false;
		}
		else if ( !deadline.equals( other.deadline ) )
			return false;
		return true;
	}
}
