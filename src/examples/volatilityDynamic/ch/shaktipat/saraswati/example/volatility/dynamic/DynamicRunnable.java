package ch.shaktipat.saraswati.example.volatility.dynamic;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class DynamicRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String className;
	
	public DynamicRunnable( String className )
	{
		super();
		this.className = className;
	}

	@Override
	public void run()
	{
		if ( className.equals( StableUser.class.getSimpleName() ) )
		{
			StableUser client = new StableUser();
			client.invokeFromStable();
		}
		else
		{
			VolatileUser client = new VolatileUser();
			client.invokeFromVolatile();
		}
	}
}
