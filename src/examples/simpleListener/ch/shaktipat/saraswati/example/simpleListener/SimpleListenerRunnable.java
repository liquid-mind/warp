package ch.shaktipat.saraswati.example.simpleListener;

import java.io.Serializable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class SimpleListenerRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( SimpleListenerRunnable.class.getName() );
	
	private String msg;

	public SimpleListenerRunnable( String msg )
	{
		super();
		this.msg = msg;
	}

	@Override
	public void run()
	{
		logger.info( "Before listening" );
		Event event = PProcessStatic.currentProcess().listen();
		logger.info( "After listening; msg=" + msg + event.getProperty( "My Property" ) );
	}
}
