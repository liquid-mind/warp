package ch.shaktipat.saraswati.internal.web.controller.pprocess;

import java.util.Arrays;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessStateMachineModel;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class PProcessStateMachineController extends PProcessController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessStateMachineModel setupModel()
	{
		PProcessManager manager = PEnvironmentFactory.createLocal().getPProcessManager();
		PProcessHandle handle = manager.findByOID( getPObjectOIDParameter() );

		PProcessStateMachineModel model = new PProcessStateMachineModel(
			true,
			handle.getOID(),
			handle.getName(),
			handle.getCreateDate(),
			handle.getOwningPrincipal(),
			getState( handle ),
			getVolatility( handle ),
			Arrays.asList( handle.getStates() ) );
		
		return model;
	}
}
