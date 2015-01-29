package ch.shaktipat.saraswati.example.helloWorld;

import java.io.Serializable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class HelloWorldRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( HelloWorldRunnable.class.getName() );

	@Override
	public void run()
	{
		logger.info( "hello world!" );
	}
}
