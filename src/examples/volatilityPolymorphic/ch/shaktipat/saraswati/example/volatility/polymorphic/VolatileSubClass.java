package ch.shaktipat.saraswati.example.volatility.polymorphic;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;


@PersistentClass
public class VolatileSubClass extends SuperClass
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatileSubClass.class.getName() );
	
	@Override
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void method1()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
	
	@Override
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void method2()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
	
	@Override
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void method3()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
		
		try
		{
			super.method1();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}

		super.method2();
	}
}
