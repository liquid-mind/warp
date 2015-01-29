package ch.shaktipat.saraswati.internal.web.controller;

import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.model.Model;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public abstract class Controller
{
	public static final String OID_PARAM = "oid";
	public static final String SEARCH_PARAM = "search";
	public static final String COMMAND_PARAM = "command";

	public static final String SUSPEND_COMMAND = "suspend";
	public static final String RESUME_COMMAND = "resume";
	public static final String CANCEL_COMMAND = "cancel";
	public static final String DESTROY_COMMAND = "destroy";
	public static final String RESTART_COMMAND = "restart";
	public static final String SHUTDOWN_COMMAND = "shutdown";

	private Model model;
	
	public Controller()
	{
		super();
	}
	
	protected abstract < T extends Model > T setupModel();

	@SuppressWarnings( "unchecked" )
	public < T extends Model > T getModel()
	{
		if ( model == null )
			model = setupModel();
		
		return (T)model;
	}

	public String getSearchParameter()
	{
		String searchParam = SaraswatiServletContext.getParameter( SEARCH_PARAM );
		
		if ( searchParam.isEmpty() )
			searchParam = ".*";
		else
			searchParam = searchParam.replaceAll( "\\*", ".*" );
		
		return searchParam;
	}

	public String getCommand()
	{
		return SaraswatiServletContext.getParameter( COMMAND_PARAM );
	}

	public abstract PObjectOID getPObjectOIDParameter();
}
