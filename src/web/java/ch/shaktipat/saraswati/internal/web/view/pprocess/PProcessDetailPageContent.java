package ch.shaktipat.saraswati.internal.web.view.pprocess;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessController;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessDetailController;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessDetailModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.ProcessResult;
import ch.shaktipat.saraswati.internal.web.transform.ClassTransformer;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.ExceptionTransformer;
import ch.shaktipat.saraswati.internal.web.transform.OnOffTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PProcessOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PrincipalTransformer;
import ch.shaktipat.saraswati.internal.web.transform.StackTraceTransformer;
import ch.shaktipat.saraswati.internal.web.transform.StringArrayTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.transform.VolatilityTransformer;
import ch.shaktipat.saraswati.internal.web.view.DetailPageContent;
import ch.shaktipat.saraswati.internal.web.view.peventqueue.PEventQueueDetailPage;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;


public class PProcessDetailPageContent extends DetailPageContent
{
	public PProcessDetailPageContent( Controller controller )
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
		PProcessOID oid = getController().getPObjectOIDParameter();
		
		if ( command.isEmpty() )
			return;
		
		String msg = null;
		
		if ( command.equals( PProcessDetailController.SUSPEND_COMMAND ) )
			msg = ( commandSuccess ? "Process was suspended." : "Error trying to suspend process." );
		else if ( command.equals( PProcessDetailController.RESUME_COMMAND ) )
			msg = ( commandSuccess ? "Process was resumed." : "Error trying to resume process." );
		else if ( command.equals( PProcessDetailController.CANCEL_COMMAND ) )
			msg = ( commandSuccess ? "Process was cancelled." : "Error trying to cancel process." );
		else if ( command.equals( Controller.DESTROY_COMMAND ) )
			msg = ( commandSuccess ? "Process with OID " + oid + " was destroyed." : "Error trying to destroy process." );
		
		if ( commandSuccess )
			Writer.write( "<div class='alert alert-success'>" + msg + "</div>" );
		else
			Writer.write( "<div class='alert alert-danger'>" + msg + "</div>" );
	}
	
	protected void renderDetails()
	{
		if ( getController().getCommand().equals( Controller.DESTROY_COMMAND ) )
			return;

		Principal principal = getModel().getOwner();
		PProcessOID oid = getModel().getOid();
		String keyState = getModel().getState();
		String[] otherStates = getStates();
		
		renderDetail( "OID", Transformation.getString( PProcessOIDTransformer.class, oid ) );
		renderDetail( "Name", getModel().getName() );
		renderDetail( "Owner", ( principal == null ? "NA" : Transformation.getString( PrincipalTransformer.class, principal ) ) );
		renderDetail( "Create Date", Transformation.getString( DateTransformer.class, getModel().getCreateDate() ) );
		renderDetail( "Last Activity Date", Transformation.getString( DateTransformer.class, getModel().getLastActivityDate() ) );
		renderDetailWithLink( "States",  "<strong>" + keyState + "</strong>, " + Transformation.getString( StringArrayTransformer.class, otherStates ), "/PProcessStateMachinePage?oid=" + oid );
		renderDetail( "Volatility", getModel().getVolatility() );
		renderDetail( "Initial Volatility", Transformation.getString( VolatilityTransformer.class, getModel().getInitialVolatility() ) );
		renderDetail( "Auto Checkpointing", Transformation.getString( OnOffTransformer.class, getModel().getAutoCheckpointing() ) );
		renderDetail( "Destroy On Completion", Transformation.getString( OnOffTransformer.class, getModel().getDestroyOnCompletion() ) );
		renderDetail( "Runnable Type", Transformation.getString( ClassTransformer.class, getModel().getRunnableType() ) );
		renderDetail( "Callable Type", Transformation.getString( ClassTransformer.class, getModel().getCallableType() ) );
		renderOwnableObjects( "Event Queues", getModel().getpEventQueues() );
		renderOwnableObjects( "Event Topics", getModel().getpEventTopics() );
		
		// TODO: Add fields: parent, children, isPersistent
		// TODO: Add ability to suspend, resume, cancel process

		renderReturnValueException();
		renderExpandableDetail( "Stack Trace", Transformation.getString( StackTraceTransformer.class, getModel().getStackTrace() ) );
		renderExpandableDetail( "System Log", getModel().getSystemLog() );
		renderExpandableDetail( "Application Log", getModel().getApplicationLog() );
	}

	private void renderReturnValueException()
	{
		ProcessResult result = getModel().getProcessResult();
		
		if ( result != null )
		{
			if ( result.getRetVal() == null )
				renderDetail( "Return Value", "null" );
			else
				renderExpandableDetail( "Return Value", result.getRetVal().toString() );
			
			if ( result.getException() == null )
				renderDetail( "Exception", "null" );
			else
				renderExpandableDetail( "Exception", (String)Transformation.getString( ExceptionTransformer.class, result.getException() ) );
		}
	}

	private void renderOwnableObjects( String fieldTitle, List< PObjectModel > ownableObjects )
	{
		for ( PObjectModel relatedObj : getModel().getpEventQueues() )
		{
			String link = SaraswatiServlet.getPageURL( PEventQueueDetailPage.class ) + "?" + Controller.OID_PARAM + "=" + relatedObj.getOid();
			renderDetailWithLink( fieldTitle, relatedObj.getName() + " (" + relatedObj.getOid() + ")", link );
		}
	}

	private String[] getStates()
	{
		List< String > allStatesAsList = new ArrayList< String >( Arrays.asList( getModel().getAllStates() ) );
		allStatesAsList.remove( getModel().getState() );
		allStatesAsList.remove( PersistentProcessStateMachine.VOLATILITY_STATE );
		allStatesAsList.remove( PersistentProcessStateMachine.VOLATILE_STATE );
		allStatesAsList.remove( PersistentProcessStateMachine.STABLE_STATE );
		String[] otherStates = allStatesAsList.toArray( new String[ allStatesAsList.size() ] );
		
		return otherStates;
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessDetailModel getModel()
	{
		return super.getModel();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessController getController()
	{
		return super.getController();
	}
}
