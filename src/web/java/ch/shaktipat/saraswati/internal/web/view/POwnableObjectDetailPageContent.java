package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.POwnableObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public abstract class POwnableObjectDetailPageContent extends DetailPageContent
{
	public POwnableObjectDetailPageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderSuccess()
	{
		renderCommandSuccess();
		renderDetails();
	}

	private void renderCommandSuccess()
	{
		String command = getController().getCommand();
		boolean commandSuccess = getModel().getCommandSuccess();
		PObjectOID oid = getController().getPObjectOIDParameter();
		
		if ( command.isEmpty() )
			return;
		
		if ( commandSuccess )
			Writer.write( "<div class='alert alert-success'>" + getDestroySuccessMessage( oid ) + "</div>" );
		else
			Writer.write( "<div class='alert alert-danger'>" + getDestroyFailureMessage() + "</div>" );
	}
	
	protected abstract String getDestroySuccessMessage( PObjectOID oid );
	protected abstract String getDestroyFailureMessage();
	protected abstract void renderDetails();

	@SuppressWarnings( "unchecked" )
	@Override
	protected POwnableObjectModel getModel()
	{
		return super.getModel();
	}
}
