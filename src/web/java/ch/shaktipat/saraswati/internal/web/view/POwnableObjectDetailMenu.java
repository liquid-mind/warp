package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.POwnableObjectModel;

public abstract class POwnableObjectDetailMenu extends Menu
{
	private String destroyDialogID = getElementID();
	
	public POwnableObjectDetailMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderPageSpecificButtons()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;
		
		Writer.write( "<div class='btn-group' data-toggle='buttons' style='padding: 10px;'>" );

		renderCommandButton( getToolTipText(), ENABLED_STATE, DESTROY_CLASS, destroyDialogID );
		
		Writer.write( "</div>" );
	}

	@Override
	protected void renderPageSpecificModals()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;

		renderModal( Controller.DESTROY_COMMAND, getModel().getOid(), "Confirm Destroy", getConfirmDestroyMessage(), "Destroy", getCommandTargetClass(), destroyDialogID );
	}

	protected abstract String getToolTipText();
	protected abstract String getConfirmDestroyMessage();
	protected abstract Class< ? > getCommandTargetClass();

	@SuppressWarnings( "unchecked" )
	@Override
	protected POwnableObjectModel getModel()
	{
		return super.getModel();
	}
}
