package ch.shaktipat.saraswati.internal.pobject.aux.event;

import java.io.Serializable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import ch.shaktipat.saraswati.common.Event;

public class EventFilter implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static ScriptEngine engine;
	
	static
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName( "JavaScript" );
	}
	
	private String filterExpression;

	public EventFilter( String filterExpression )
	{
		super();
		this.filterExpression = filterExpression;
	}
	
	public boolean matches( Event event )
	{
		boolean matches = false;
		
		if ( filterExpression == null || filterExpression.equals( "" ) )
		{
			matches = true;
		}
		else
		{
			try
			{
				// The synchronized block is to make sure that the commands
				// are done atomically; otherwise, event could be changed
				// by one thread while another evaluates the expression.
				synchronized( EventFilter.class )
				{
					engine.put( "event", event );
					matches = (boolean)engine.eval( filterExpression );
				}
			}
			catch ( ScriptException e )
			{
				// Ignore the exception and allow matches to be null.
			}
		}
		
		return matches;
	}

	@Override
	public String toString()
	{
		return "EventFilter [filterExpression=" + filterExpression + "]";
	}

	public String getFilterExpression()
	{
		return filterExpression;
	}
}
