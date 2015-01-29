package ch.shaktipat.saraswati.internal.web.controller.pprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessDetailModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.ProcessResult;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public class PProcessDetailController extends PProcessController
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessDetailModel setupModel()
	{
		String command = getCommand();
		boolean commandSuccess = true;
		PProcessManager manager = PEnvironmentFactory.createLocal().getPProcessManager();
		PProcessHandle handle = manager.findByOID( getPObjectOIDParameter() );
		PProcessDetailModel model = null;
		
		if ( command.equals( SUSPEND_COMMAND ) )
			handle.suspend();
		else if ( command.equals( RESUME_COMMAND ) )
			handle.resume();
		else if ( command.equals( CANCEL_COMMAND ) )
			handle.cancel();
		else if ( command.equals( DESTROY_COMMAND ) )
			handle.destroy();
		else if ( !command.isEmpty() )
			throw new RuntimeException( "Unexpected command: " + command );
		
		if ( command.equals( DESTROY_COMMAND ) )
			model = new PProcessDetailModel( commandSuccess );
		else
			model = new PProcessDetailModel(
				commandSuccess,
				handle.getOID(),
				handle.getName(),
				handle.getCreateDate(),
				handle.getOwningPrincipal(),
				getState( handle ),
				getVolatility( handle ),
				handle.getLastActivityDate(),
				handle.getInitialVolatility(),
				handle.getDestroyOnCompletion(),
				handle.getAutoCheckpointing(),
				handle.getStates(),
				getProcessResult( handle ),
				handle.getSystemLog(),
				handle.getApplicationLog(),
				getOwnableObjects( PEventQueueOID.class, handle.getOwnableObjects() ),
				getOwnableObjects( PEventTopicOID.class, handle.getOwnableObjects() ),
				handle.getStackTrace(),
				handle.getRunnableType(),
				handle.getCallableType() );
		
		return model;
	}
	
	private List< PObjectModel > getOwnableObjects( Class< ? > ownableObjectOIDClass, POwnableObject[] ownableObjects )
	{
		List< PObjectModel > ownableObjectsResult = new ArrayList< PObjectModel >();
		
		for ( POwnableObject ownableObject : ownableObjects )
		{
			if ( ownableObjectOIDClass.isAssignableFrom( ownableObject.getOID().getClass() ) )
				ownableObjectsResult.add( new PObjectModel( true, ownableObject.getOID(), ownableObject.getName(), null ) );
		}
		
		return ownableObjectsResult;
	}
	
	private ProcessResult getProcessResult( PProcessHandle handle )
	{
		ProcessResult processResult = new ProcessResult();
		
		if ( handle.isInState( PersistentProcessStateMachine.TERMINATED_STATE ) )
		{
			try
			{
				processResult.setRetVal( handle.getFuture().get() );
			}
			catch ( InterruptedException e )
			{}
			catch ( ExecutionException e )
			{
				processResult.setException( e.getCause() );
			}
		}
		
		return processResult;
	}
}
