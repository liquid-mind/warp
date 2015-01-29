package ch.shaktipat.saraswati.example.volatility.dynamic;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;

@PersistentClass
public class VolatileUser
{
	private static final Logger logger = PProcessStatic.getApplicationLogger( VolatileUser.class.getName() );
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void invokeFromVolatile()
	{
		DynamicService service = new DynamicService( 42 );
		
		try
		{
			service.stateDependantMethod1();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}
		
		service.stateDependantMethod2();
		service.stateDependantMethod3();

		DynamicService service2 = new DynamicService( 106 );
		
		try
		{
			service2.stateDependantMethod1();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}

		try
		{
			service2.stateDependantMethod2();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}

		try
		{
			service2.stateDependantMethod3();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}
	}
}
