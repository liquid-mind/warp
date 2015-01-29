package ch.shaktipat.saraswati.internal.web.model.peventtopic;

import java.util.UUID;

import ch.shaktipat.saraswati.internal.web.model.Model;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public class SubscriptionModel extends Model
{
	private UUID subscriptionOID;
	private PEventQueueOID targetEventQueueOID;
	private String targetEventQueueName;
	private String filterExpression;
	private boolean oneTimeOnly;
	
	public SubscriptionModel(  boolean commandSuccess, UUID subscriptionOID, PEventQueueOID targetEventQueueOID, String targetEventQueueName, String filterExpression, boolean oneTimeOnly )
	{
		super( commandSuccess );
		this.subscriptionOID = subscriptionOID;
		this.targetEventQueueOID = targetEventQueueOID;
		this.targetEventQueueName = targetEventQueueName;
		this.filterExpression = filterExpression;
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

	public String getTargetEventQueueName()
	{
		return targetEventQueueName;
	}

	public void setTargetEventQueueName( String targetEventQueueName )
	{
		this.targetEventQueueName = targetEventQueueName;
	}

	public String getFilterExpression()
	{
		return filterExpression;
	}

	public void setFilterExpression( String filterExpression )
	{
		this.filterExpression = filterExpression;
	}

	public boolean isOneTimeOnly()
	{
		return oneTimeOnly;
	}

	public void setOneTimeOnly( boolean oneTimeOnly )
	{
		this.oneTimeOnly = oneTimeOnly;
	}
}
