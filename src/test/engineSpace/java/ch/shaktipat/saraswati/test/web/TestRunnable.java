package ch.shaktipat.saraswati.test.web;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class TestRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( TestRunnable.class.getName() );
	
	public static final String DO_NOTHING = "DO_NOTHING";
	public static final String THROW_EXCEPTION = "THROW_EXCEPTION";
	public static final String LISTEN_FOR_EVENT = "LISTEN_FOR_EVENT";
	
	private String action;

	public TestRunnable( String action )
	{
		super();
		this.action = action;
	}

	@Override
	public void run()
	{
		PProcess thisProcess = PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess();
		
		logger.info( "action == " + action );
		
		if ( action.equals( DO_NOTHING ) )
		{
			// Do nothing;
		}
		else if ( action.equals( THROW_EXCEPTION ) )
		{
			throw new RuntimeException( "Test Exception" );
		}
		else if ( action.equals( LISTEN_FOR_EVENT ) )
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime( new Date() );
			cal.add( Calendar.DAY_OF_YEAR, 1 );
			thisProcess.listen( "event.toString().equals( 'MyEvent' )", cal.getTime() );
		}
	}
}
