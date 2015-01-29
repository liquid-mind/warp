package ch.shaktipat.saraswati.internal.web.model.peventtopic;

import java.util.Date;
import java.util.Set;

import ch.shaktipat.saraswati.internal.web.model.POwnableObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PEventTopicDetailModel extends POwnableObjectModel
{
	private Set< SubscriptionModel > subscriptions;
	
	public PEventTopicDetailModel(
			boolean commandSuccess,
			PObjectOID oid,
			String name,
			Date createDate,
			PProcessOID owningProcessOID,
			String owningProcessName,
			Set< SubscriptionModel > subscriptions )
	{
		super( commandSuccess, oid, name, createDate, owningProcessOID, owningProcessName );
		this.subscriptions = subscriptions;
	}
	
	public PEventTopicDetailModel( boolean commandSuccess )
	{
		super( commandSuccess, null, null, null, null, null );
	}

	public Set< SubscriptionModel > getSubscriptions()
	{
		return subscriptions;
	}

	public void setSubscriptions( Set< SubscriptionModel > subscriptions )
	{
		this.subscriptions = subscriptions;
	}
}
