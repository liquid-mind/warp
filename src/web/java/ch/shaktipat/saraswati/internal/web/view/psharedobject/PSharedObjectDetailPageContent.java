package ch.shaktipat.saraswati.internal.web.view.psharedobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.psharedobject.PSharedObjectDetailModel;
import ch.shaktipat.saraswati.internal.web.transform.ClassTransformer;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.InterfaceTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PProcessOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PSharedObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailPageContent;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessDetailPage;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;


public class PSharedObjectDetailPageContent extends POwnableObjectDetailPageContent
{
	public PSharedObjectDetailPageContent( Controller controller )
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
		Class< ? > sharedObjectType = getModel().getSharedObjectType();
		
		renderDetail( "OID", Transformation.getString( PSharedObjectOIDTransformer.class, getModel().getOid() ) );
		renderDetail( "Name", getModel().getName() );
		renderDetail( "Create Date", Transformation.getString( DateTransformer.class, getModel().getCreateDate() ) );
		renderDetailWithLink( "Owning Process", owningProcess, owningProcessLink );
		renderDetail( "Shared Object Type", Transformation.getString( ClassTransformer.class, sharedObjectType ) );
		renderExpandableDetail( "Shared Object Interfaces", getSharedObjectInterfaces( sharedObjectType ) );
	}
	
	private String getSharedObjectInterfaces( Class< ? > sharedObjectType )
	{
		Class< ? >[] interfaces = sharedObjectType.getInterfaces();
		List< String > classesAsStrings = new ArrayList< String >();

		for ( Class< ? > interfaceInstance : interfaces )
		{
			if ( interfaceInstance.equals( Serializable.class ) )
				continue;
			
			classesAsStrings.add( Transformation.getString( InterfaceTransformer.class, interfaceInstance ) );
		}
		
		return StringUtils.join( classesAsStrings, "\n" );
	}

	@Override
	protected PSharedObjectDetailModel getModel()
	{
		return (PSharedObjectDetailModel)super.getModel();
	}

	@Override
	protected String getDestroySuccessMessage( PObjectOID oid )
	{
		return "Shared object with OID " + oid + " was destroyed.";
	}

	@Override
	protected String getDestroyFailureMessage()
	{
		return "Error trying to destroy shared object.";
	}
}
