package ch.shaktipat.saraswati.common;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public abstract class AbstractEvent implements Event
{
	private static final long serialVersionUID = 1L;
	
	private Date createDate;
	private PProcessOID senderOID;
	private UUID subscriptionID;
	private PEventQueueOID pEventQueueOID;
	private Properties properties;

	public AbstractEvent()
	{
		createDate = new Date();
		properties = new Properties();
	}

	@Override
	public PProcessOID getSenderOID()
	{
		return senderOID;
	}

	@Override
	public void setSenderOID( PProcessOID senderOID )
	{
		this.senderOID = senderOID;
	}

	@Override
	public String getProperty( String key )
	{
		return properties.getProperty( key );
	}

	@Override
	public void setProperty( String key, String value )
	{
		properties.setProperty( key, value );
	}

	@Override
	public UUID getSubscriptionOID()
	{
		return subscriptionID;
	}

	@Override
	public void setSubscriptionOID( UUID subscriptionOID )
	{
		this.subscriptionID = subscriptionOID;
	}

	@Override
	public PEventQueueOID getPEventQueueOID()
	{
		return pEventQueueOID;
	}

	@Override
	public void setPEventQueueOID( PEventQueueOID pEventQueueOID )
	{
		this.pEventQueueOID = pEventQueueOID;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	@Override
	public String toString()
	{
		return "AbstractEvent [senderOID=" + senderOID + ", subscriptionOID=" + subscriptionID + ", persistentEventQueueOID=" + pEventQueueOID + ", properties=" + properties + "]";
	}
}
