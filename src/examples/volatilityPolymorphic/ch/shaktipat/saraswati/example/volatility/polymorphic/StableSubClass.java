package ch.shaktipat.saraswati.example.volatility.polymorphic;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;


@PersistentClass
public class StableSubClass extends SuperClass
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( StableSubClass.class.getName() );
	
	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void method1()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
	
//  ILLEGAL: class verifier throws VolatilityException.
//	@Override
//	@PersistentMethod( volatility=Volatility.STABLE )
//	public void method2()
//	{
//		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
//	}
	
	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void method3()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
		super.method1();
		super.method2();
	}
}
