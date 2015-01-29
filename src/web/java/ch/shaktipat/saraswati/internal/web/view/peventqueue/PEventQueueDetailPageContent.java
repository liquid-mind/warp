package ch.shaktipat.saraswati.internal.web.view.peventqueue;

import java.util.ArrayList;
import java.util.List;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.peventqueue.PEventQueueDetailModel;
import ch.shaktipat.saraswati.internal.web.transform.ClassTransformer;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PEventQueueOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PProcessOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailPageContent;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessDetailPage;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;


public class PEventQueueDetailPageContent extends POwnableObjectDetailPageContent
{
	public PEventQueueDetailPageContent( Controller controller )
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
		String listeningProcessOID = Transformation.getString( PProcessOIDTransformer.class, getModel().getListeningProcessOID() );
		String listeningProcess = ( getModel().getListeningProcessName() == null ? null : getModel().getListeningProcessName() + " (" + listeningProcessOID + ")" );
		String listeningProcessLink = SaraswatiServlet.getPageURL( PProcessDetailPage.class ) + "?" + Controller.OID_PARAM + "=" + listeningProcessOID;
		
		renderDetail( "OID", Transformation.getString( PEventQueueOIDTransformer.class, getModel().getOid() ) );
		renderDetail( "Name", getModel().getName() );
		renderDetail( "Create Date", Transformation.getString( DateTransformer.class, getModel().getCreateDate() ) );
		renderDetailWithLink( "Owning Process", owningProcess, owningProcessLink );
		renderDetailWithLink( "Listing Process", listeningProcess, listeningProcessLink );
		renderDetail( "Listening Process Filter", getModel().getListeningFilter() );
		renderDetail( "Listening Process Timeout", Transformation.getString( DateTransformer.class, getModel().getListeningTimeout() ) );
		renderDetails( "Queued Event Types", transformClassListToStringList( getModel().getQueuedEventTypes() ) );
	}
	
	private List< String > transformClassListToStringList( List< Class< ? > > classList )
	{
		List< String > stringList = null;
		
		if ( classList != null && !classList.isEmpty() )
		{
			stringList = new ArrayList< String >();
			
			for ( Class< ? > aClass : classList )
				stringList.add( Transformation.getString( ClassTransformer.class, aClass ) );
		}
		
		return stringList;
	}

	@Override
	protected PEventQueueDetailModel getModel()
	{
		return (PEventQueueDetailModel)super.getModel();
	}

	@Override
	protected String getDestroySuccessMessage( PObjectOID oid )
	{
		return "Event queue with OID " + oid + " was destroyed.";
	}

	@Override
	protected String getDestroyFailureMessage()
	{
		return "Error trying to destroy event queue.";
	}
}
