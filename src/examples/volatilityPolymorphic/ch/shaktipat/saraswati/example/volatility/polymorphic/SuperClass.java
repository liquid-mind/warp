package ch.shaktipat.saraswati.example.volatility.polymorphic;

import java.io.Serializable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public abstract class SuperClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( SuperClass.class.getName() );
	
	@PersistentMethod( volatility=Volatility.STABLE )
	public void method1()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void method2()
	{
		logger.info( "Volatility=" + PProcessStatic.currentProcess().getVolatility() );
	}
	
	public abstract void method3();
}
