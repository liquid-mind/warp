package ch.shaktipat.saraswati.internal.web.view.peventtopic;

import java.util.Set;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.PEventTopicDetailModel;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.SubscriptionModel;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PEventTopicOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PProcessOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.transform.UUIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.YesNoTransformer;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailPageContent;
import ch.shaktipat.saraswati.internal.web.view.peventqueue.PEventQueueDetailPage;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessDetailPage;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;


public class PEventTopicDetailPageContent extends POwnableObjectDetailPageContent
{

	public PEventTopicDetailPageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderDetails()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;

		String owningProcessOID = Transformation.getString( PProcessOIDTransformer.class, getModel().getOwningProcessOID() );
		String owningProcess = ( getModel().getOwningProcessName() == null ? null : getModel().getOwningProcessName() + " (" + owningProcessOID + ")" );
		String owningProcessLink = SaraswatiServlet.getPageURL( PProcessDetailPage.class ) + "?" + Controller.OID_PARAM + "=" + owningProcessOID;
		
		renderDetail( "OID", Transformation.getString( PEventTopicOIDTransformer.class, getModel().getOid() ) );
		renderDetail( "Name", getModel().getName() );
		renderDetail( "Create Date", Transformation.getString( DateTransformer.class, getModel().getCreateDate() ) );
		renderDetailWithLink( "Owning Process", owningProcess, owningProcessLink );
		
		if ( getModel().getSubscriptions().size() > 0 )
			renderTable();
	}

	@Override
	protected PEventTopicDetailModel getModel()
	{
		return (PEventTopicDetailModel)super.getModel();
	}

	private void renderTable()
	{
		Set< SubscriptionModel > subscriptions = getModel().getSubscriptions();
		
		if ( getModel().getSubscriptions().size() == 0 )
			return;
		
		Writer.write( "<table class='table table-striped table-hover'>" );
		Writer.write( "<thead>" );
		Writer.write( "<tr>" );
		
		Writer.write( "<th>Subscription OID</th>" );
		Writer.write( "<th>Target Event Queue OID</th>" );
		Writer.write( "<th>Target Event Queue Name</th>" );
		Writer.write( "<th>Event Filter</th>" );
		Writer.write( "<th>Is One Time Only</th>" );
		
		Writer.write( "</tr>" );
		Writer.write( "</thead>" );

		Writer.write( "<tbody>" );
		
		for ( SubscriptionModel subscription : subscriptions )
		{
			Writer.write( "<tr>" );
			
			String targetEventQueueOID = Transformation.getString( PObjectOIDTransformer.class, subscription.getTargetEventQueueOID() );
			String targetEventQueueLink = SaraswatiServlet.getPageURL( PEventQueueDetailPage.class ) + "?" + Controller.OID_PARAM + "=" + targetEventQueueOID;
			
			Writer.write( "<td>" + Transformation.getString( UUIDTransformer.class, subscription.getSubscriptionOID() ) + "</td>" );
			Writer.write( "<td><a href='" + targetEventQueueLink + "'>" + targetEventQueueOID + "</a></td>" );
			Writer.write( "<td><a href='" + targetEventQueueLink + "'>" + subscription.getTargetEventQueueName() + "</a></td>" );
			Writer.write( "<td>" + subscription.getFilterExpression() + "</td>" );
			Writer.write( "<td>" + Transformation.getString( YesNoTransformer.class, subscription.isOneTimeOnly() ) + "</td>" );
			
			Writer.write( "</tr>" );
		}
		
		Writer.write( "</tbody>" );
		
		Writer.write( "</table>" );
	}	

	@Override
	protected String getDestroySuccessMessage( PObjectOID oid )
	{
		return "Event topic with OID " + oid + " was destroyed.";
	}

	@Override
	protected String getDestroyFailureMessage()
	{
		return "Error trying to destroy event topic.";
	}
}
