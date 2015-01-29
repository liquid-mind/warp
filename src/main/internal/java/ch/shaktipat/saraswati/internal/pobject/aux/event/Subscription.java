package ch.shaktipat.saraswati.internal.pobject.aux.event;

import java.io.Serializable;
import java.util.UUID;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public class Subscription implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID subscriptionOID;
	private PEventQueueOID targetEventQueueOID;
	private EventFilter eventFilter;
	private boolean oneTimeOnly;
	
	public Subscription( PEventQueueOID targetEventQueueOID, EventFilter eventFilter, boolean oneTimeOnly )
	{
		super();
		this.subscriptionOID = UUID.randomUUID();
		this.targetEventQueueOID = targetEventQueueOID;
		this.eventFilter = eventFilter;
		this.oneTimeOnly = oneTimeOnly;
	}

	public UUID getSubscriptionOID()
	{
		return subscriptionOID;
	}

	public void setSubscriptionOID( UUID subscriptionOID )
	{
		this.subscriptionOID = subscriptionOID;
	}

	public PEventQueueOID getTargetEventQueueOID()
	{
		return targetEventQueueOID;
	}

	public void setTargetEventQueueOID( PEventQueueOID targetEventQueueOID )
	{
		this.targetEventQueueOID = targetEventQueueOID;
	}

	public boolean isOneTimeOnly()
	{
		return oneTimeOnly;
	}

	public void setOneTimeOnly( boolean oneTimeOnly )
	{
		this.oneTimeOnly = oneTimeOnly;
	}

	public EventFilter getEventFilter()
	{
		return eventFilter;
	}

	public void setEventFilter( EventFilter eventFilter )
	{
		this.eventFilter = eventFilter;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( subscriptionOID == null ) ? 0 : subscriptionOID.hashCode() );
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
		Subscription other = (Subscription)obj;
		if ( subscriptionOID == null )
		{
			if ( other.subscriptionOID != null )
				return false;
		}
		else if ( !subscriptionOID.equals( other.subscriptionOID ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Subscription [subscriptionOID=" + subscriptionOID + ", targetEventQueueOID=" + targetEventQueueOID + ", eventFilter=" + eventFilter + ", oneTimeOnly=" + oneTimeOnly + "]";
	}
}
