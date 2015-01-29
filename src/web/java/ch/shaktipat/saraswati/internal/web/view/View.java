package ch.shaktipat.saraswati.internal.web.view;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.Model;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public abstract class View
{
	public static final String ENABLED_STATE = "";
	public static final String DISABLED_STATE = "disabled='disabled'";

	public static final String SUSPEND_CLASS = "glyphicon glyphicon-pause";
	public static final String RESUME_CLASS = "glyphicon glyphicon-play";
	public static final String CANCEL_CLASS = "glyphicon glyphicon-stop";
	public static final String DESTROY_CLASS = "glyphicon glyphicon-remove";
	public static final String SHUTDOWN_CLASS = "glyphicon glyphicon-off";
	public static final String RESTART_CLASS = "glyphicon glyphicon-repeat";

	private Controller controller;
	
	public View( Controller controller )
	{
		super();
		this.controller = controller;
	}

	@SuppressWarnings( "unchecked" )
	protected < T extends Controller > T getController()
	{
		return (T)controller;
	}
	public abstract void render();
	
	protected < T extends Model > T getModel()
	{
		return controller.getModel();
	}
	
	public static String getElementID()
	{
		return UUID.randomUUID().toString();
	}
	
	protected void renderCommandButton( String tooltip, String disabledState, String buttonClass, String confirmDialogId )
	{
		String commandButtonId = getElementID();
		
		Writer.write( "<button id='" + commandButtonId + "' type='button' data-toggle='tooltip' data-placement='bottom' data-title='" + tooltip + "' class='btn btn-default' " + disabledState +
			" onclick=\"$( '#" + commandButtonId + "' ).addClass( 'active' ); $('#" + confirmDialogId + "').modal('show')\"><span class='" + buttonClass + "'></button>" );
	}
	
	protected void renderModal( String command, PObjectOID oid, String title, String msg, String commandButtonName, Class< ? > detailPageClass, String confirmDialogId )
	{
		Set< String > params = new HashSet< String >();
		
		params.add( "command=" + command );
		
		if ( oid != null )
			params.add( "oid=" + oid );
		
		String commandLink = SaraswatiServlet.getPageURL( detailPageClass ) + "?" + StringUtils.join( params, "&" );
		
		Writer.write( "<div class='modal fade' id='" + confirmDialogId + "' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>" );
		Writer.write( "<div class='modal-dialog'>" );
		Writer.write( "<div class='modal-content'>" );
		Writer.write( "<div class='modal-header'>" );
		Writer.write( "<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>" );
		Writer.write( "<h4 class='modal-title' id='myModalLabel'>" + title + "</h4>" );
		Writer.write( "</div>" );
		Writer.write( "<div class='modal-body'>" );
		Writer.write( msg );
		Writer.write( "</div>" );
		Writer.write( "<div class='modal-footer'>" );
		Writer.write( "<button type='button' class='btn btn-default' data-dismiss='modal'>Cancel</button>" );
		Writer.write( "<button type='button' class='btn btn-default' onclick=\"location.href='" + commandLink + "'\">" + commandButtonName + "</button>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
	}
}
