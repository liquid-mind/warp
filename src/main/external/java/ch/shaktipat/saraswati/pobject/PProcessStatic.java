package ch.shaktipat.saraswati.pobject;

import java.util.logging.Logger;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;

public class PProcessStatic
{
	public static PProcess currentProcess()
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess();
	}
	
	public static Logger getApplicationLogger( String name )
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getApplicationLogger( name );
	}
	
	public static Logger getApplicationLogger( String name, boolean useParentHandlers )
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getApplicationLogger( name, useParentHandlers );
	}
	
	public static Logger getSystemLogger( String name )
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getSystemLogger( name );
	}
	
	public static Logger getSystemLogger( String name, boolean useParentHandlers )
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getSystemLogger( name, useParentHandlers );
	}
}
