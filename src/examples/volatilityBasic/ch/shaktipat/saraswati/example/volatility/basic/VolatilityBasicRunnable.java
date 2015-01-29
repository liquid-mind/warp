package ch.shaktipat.saraswati.example.volatility.basic;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class VolatilityBasicRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatilityBasicRunnable.class.getName() );

	private String fileName;
	
	public VolatilityBasicRunnable( String fileName )
	{
		super();
		this.fileName = fileName;
	}

	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void run()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
		accessVolatileResource();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void accessVolatileResource()
	{
		try
		{
			logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
			InputStream is = new FileInputStream( fileName );
			Reader r = new InputStreamReader( is );
			BufferedReader br = new BufferedReader( r );
			logger.info( br.readLine() );
			br.close();
		}
		catch ( IOException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}
	}
}
