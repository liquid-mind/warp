package ch.shaktipat.saraswati.internal.web.controller.psharedobject;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.psharedobject.PSharedObjectDetailModel;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;

public class PSharedObjectDetailController extends PSharedObjectController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected PSharedObjectDetailModel setupModel()
	{
		PSharedObjectManager manager = PEnvironmentFactory.createLocal().getPSharedObjectManager();
		PSharedObjectHandle handle = manager.findByOID( getPObjectOIDParameter() );
		PProcessHandle owningProcess = handle.getOwningProcess();
		String command = getCommand();
		boolean commandSuccess = true;
		PSharedObjectDetailModel model = null;

		if ( command.equals( DESTROY_COMMAND ) )
			handle.destroy();
		else if ( !command.isEmpty() )
			throw new RuntimeException( "Unexpected command: " + command );
		
		if ( command.equals( DESTROY_COMMAND ) )
			model = new PSharedObjectDetailModel( commandSuccess );
		else
			model = new PSharedObjectDetailModel(
				true,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate(),
				getProcessOID( owningProcess ),
				getProcessName( owningProcess ),
				handle.getSharedObjectClass() );
		
		return model;
	}
}
