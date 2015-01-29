package ch.shaktipat.saraswati.internal.web.view.server;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.Content;


// TODO Add additional items to server page:
// -access to server log file
// -list of persistent runnable/callable classes
public class ServerPageContent extends Content
{
	public ServerPageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		String command = getController().getCommand();
		boolean commandSuccess = getModel().getCommandSuccess();
		
		if ( command.equals( Controller.RESTART_COMMAND ) )
			renderRestartSuccess( commandSuccess );
		else if ( command.equals( Controller.SHUTDOWN_COMMAND ) )
			renderShutdownSuccess( commandSuccess );
		else
			throw new RuntimeException( "Unknown command: " + command );
	}
	
	private void renderRestartSuccess( boolean commandSuccess )
	{
		if ( commandSuccess )
			Writer.write( "<div class='alert alert-success'>Server restart command successful.</div>" );
		else
			Writer.write( "<div class='alert alert-danger'>Error trying to restart server.</div>" );
	}
	
	private void renderShutdownSuccess( boolean commandSuccess )
	{
		if ( commandSuccess )
			Writer.write( "<div class='alert alert-success'>Server shutdown command successful.</div>" );
		else
			Writer.write( "<div class='alert alert-danger'>Error trying to shutdown server.</div>" );
	}
}
