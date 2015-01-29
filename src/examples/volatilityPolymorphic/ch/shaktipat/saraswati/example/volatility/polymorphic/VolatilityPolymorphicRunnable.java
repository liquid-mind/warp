package ch.shaktipat.saraswati.example.volatility.polymorphic;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

@PersistentClass
public class VolatilityPolymorphicRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String className;

	public VolatilityPolymorphicRunnable( String className )
	{
		super();
		this.className = className;
	}

	@Override
	@PersistentMethod( volatility=Volatility.STABLE )
	public void run()
	{
		SuperClass c = null;
		
		if ( className.equals( VolatileSubClass.class.getSimpleName() ) )
			c = new VolatileSubClass();
		else
			c = new StableSubClass();
		
		c.method1();
		c.method2();
		c.method3();
	}
}
