package ch.shaktipat.saraswati.internal.web.controller.server;

import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.server.ServerModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class ServerController extends Controller
{
	@SuppressWarnings( "unchecked" )
	@Override
	protected ServerModel setupModel()
	{
		new ServerControlThread( getCommand() ).start();
		
		return new ServerModel( true );
	}

	@Override
	public PObjectOID getPObjectOIDParameter()
	{
		// refactor getPObjectOIDParameter() into subclass of Controller.
		return null;
	}
}
