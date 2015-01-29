package ch.shaktipat.exwrapper.org.apache.commons.scxml;

import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;

import ch.shaktipat.exwrapper.org.apache.commons.scxml.model.ModelExceptionWrapper;

public class SCXMLExecutorWrapper
{
	public static void go( SCXMLExecutor scxmlExecutor ) 
	{
		try
		{
			scxmlExecutor.go();
		}
		catch ( ModelException e )
		{
			throw new ModelExceptionWrapper( e );
		}
	}
	
	public static void triggerEvent( SCXMLExecutor scxmlExecutor, TriggerEvent evt ) 
	{
		try
		{
			scxmlExecutor.triggerEvent( evt );
		}
		catch ( ModelException e )
		{
			throw new ModelExceptionWrapper( e );
		}
	}
}
