package ch.shaktipat.saraswati.common;

import java.io.Serializable;
import java.util.UUID;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public interface Event extends Serializable
{
	public PProcessOID getSenderOID();
	public void setSenderOID( PProcessOID senderOID );
	public UUID getSubscriptionOID();
	public void setSubscriptionOID( UUID subscriptionID );
	public PEventQueueOID getPEventQueueOID();
	public void setPEventQueueOID( PEventQueueOID pEventQueueOID );
	public String getProperty( String key );
	public void setProperty( String key, String value );
}
