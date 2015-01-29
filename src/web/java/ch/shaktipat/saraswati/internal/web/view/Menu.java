package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.server.ServerPage;


public abstract class Menu extends View
{
	private String restartDialogID = getElementID();
	private String shutdownDialogID = getElementID();
	
	public Menu( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		Writer.write( "<div class='navbar navbar-default navbar-static-top' role='navigation'>" );
		Writer.write( "<div class='container'>" );
		Writer.write( "<div class='navbar-header'>" );
		Writer.write( "<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='.navbar-collapse'>" );
		Writer.write( "<span class='sr-only'>Toggle navigation</span>" );
		Writer.write( "<span class='icon-bar'></span>" );
		Writer.write( "<span class='icon-bar'></span>" );
		Writer.write( "<span class='icon-bar'></span>" );
		Writer.write( "</button>" );
		Writer.write( "<a class='navbar-brand' href='/'>Saraswati Admin Console</a>" );
		Writer.write( "</div>" );
		Writer.write( "<div class='navbar-collapse collapse'>" );

		Writer.write( "<div class='nav navbar-nav navbar-right'>" );
		
		renderPageSpecificButtons();
		renderRestartShutdownButtons();
		
		Writer.write( "</div>" );

		Writer.write( "</div><!--/.nav-collapse -->" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
		
		renderPageSpecificModals();
		renderRestartShutdownModals();
	}
	
	protected abstract void renderPageSpecificButtons();
	protected abstract void renderPageSpecificModals();
	
	private void renderRestartShutdownButtons()
	{
		Writer.write( "<div class='btn-group' data-toggle='buttons' style='padding: 10px;'>" );
		
		renderCommandButton( "restart server", ENABLED_STATE, RESTART_CLASS, restartDialogID );
		renderCommandButton( "shutdown server", ENABLED_STATE, SHUTDOWN_CLASS, shutdownDialogID );
		
		Writer.write( "</div>" );
	}
	
	private void renderRestartShutdownModals()
	{
		renderModal( Controller.RESTART_COMMAND, null, "Confirm Restart", "You are about to restart the server; are you sure you want to continue?", "Restart", ServerPage.class, restartDialogID );
		renderModal( Controller.SHUTDOWN_COMMAND, null, "Confirm Shutdown", "You are about to shutdown the server; are you sure you want to continue?", "Shutdown", ServerPage.class, shutdownDialogID );
	}
}
