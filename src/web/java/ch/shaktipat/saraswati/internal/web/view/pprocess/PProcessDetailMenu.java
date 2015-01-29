package ch.shaktipat.saraswati.internal.web.view.pprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessDetailController;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessDetailModel;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PProcessDetailMenu extends Menu
{
	private String suspendDialogID = getElementID();
	private String resumeDialogID = getElementID();
	private String cancelDialogID = getElementID();
	private String destroyDialogID = getElementID();

	public PProcessDetailMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderPageSpecificButtons()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;
		
		List< String > allStates = new ArrayList< String >( Arrays.asList( getModel().getAllStates() ) );
		
		Map< String, String > disabledMap = new HashMap< String, String >();

		disabledMap.put( "suspend", ENABLED_STATE );
		disabledMap.put( "resume", ENABLED_STATE );
		disabledMap.put( "cancel", ENABLED_STATE );
		disabledMap.put( "destroy", ENABLED_STATE );
		
		if ( !allStates.contains( PersistentProcessStateMachine.ANIMATED_STATE ) )
			disabledMap.put( PersistentProcessStateMachine.SUSPEND_EVENT, DISABLED_STATE );
		if ( !allStates.contains( PersistentProcessStateMachine.SUSPENDED_STATE ) )
			disabledMap.put( PersistentProcessStateMachine.RESUME_EVENT, DISABLED_STATE );
		if ( !allStates.contains( PersistentProcessStateMachine.PENDING_STATE ) )
			disabledMap.put( PersistentProcessStateMachine.CANCEL_EVENT, DISABLED_STATE );
		if ( !allStates.contains( PersistentProcessStateMachine.REFERENCABLE_STATE ) )
			disabledMap.put( PersistentProcessStateMachine.DESTROY_EVENT, DISABLED_STATE );

		Writer.write( "<div class='btn-group' data-toggle='buttons' style='padding: 10px;'>" );
		
		renderCommandButton( "suspend process", disabledMap.get( PersistentProcessStateMachine.SUSPEND_EVENT ), SUSPEND_CLASS, suspendDialogID );
		renderCommandButton( "resume process", disabledMap.get( PersistentProcessStateMachine.RESUME_EVENT ), RESUME_CLASS, resumeDialogID );
		renderCommandButton( "cancel process", disabledMap.get( PersistentProcessStateMachine.CANCEL_EVENT ), CANCEL_CLASS, cancelDialogID );
		renderCommandButton( "destroy process", disabledMap.get( PersistentProcessStateMachine.DESTROY_EVENT ), DESTROY_CLASS, destroyDialogID );
		
		Writer.write( "</div>" );
	}

	@Override
	protected void renderPageSpecificModals()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;

		PProcessOID oid = getModel().getOid();

		renderModal( Controller.SUSPEND_COMMAND, oid, "Confirm Suspend", "You are about to suspend this process. Do you want to continue?", "Suspend", PProcessDetailPage.class, suspendDialogID );
		renderModal( Controller.RESUME_COMMAND, oid, "Confirm Resume", "You are about to resume this process. Do you want to continue?", "Resume", PProcessDetailPage.class, resumeDialogID );
		renderModal( Controller.CANCEL_COMMAND, oid, "Confirm Cancel", "You are about to cancel this process. Do you want to continue?", "Continue", PProcessDetailPage.class, cancelDialogID );
		renderModal( Controller.DESTROY_COMMAND, oid, "Confirm Destroy", "You are about to destroy this process; this cannot be undone. Do you want to continue?", "Destroy", PProcessDetailPage.class, destroyDialogID );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessDetailController getController()
	{
		return super.getController();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessDetailModel getModel()
	{
		return super.getModel();
	}
}
