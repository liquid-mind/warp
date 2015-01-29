package ch.shaktipat.saraswati.example.volatility.dynamic;

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;

@PersistentClass
public class StableUser
{
	private static final Logger logger = PProcessStatic.getApplicationLogger( StableUser.class.getName() );

	@PersistentMethod( volatility=Volatility.STABLE )
	public void invokeFromStable()
	{
		DynamicService service1 = new DynamicService( 42 );
		service1.stateDependantMethod1();
		service1.stateDependantMethod2();
		service1.stateDependantMethod3();
		
		DynamicService service2 = new DynamicService( 106 );
		service2.stateDependantMethod1();
		
		try
		{
			service2.stateDependantMethod2();
		}
		catch ( VolatilityException e )
		{
			logger.log( Level.SEVERE, "Caught exception", e );
		}
		
		service2.stateDependantMethod3();
	}
}
