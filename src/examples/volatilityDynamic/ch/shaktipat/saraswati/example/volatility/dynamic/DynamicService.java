package ch.shaktipat.saraswati.example.volatility.dynamic;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;


@PersistentClass
public class DynamicService
{
	private int value;

	public DynamicService( int value )
	{
		super();
		this.value = value;
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	public void stateDependantMethod1()
	{
		if ( value > 100 )
			stableMethod();
		else
			volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	public void stateDependantMethod2()
	{
		if ( value > 100 )
			stableMethod();
		else
			volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.DYNAMIC )
	public void stateDependantMethod3()
	{
		if ( value > 100 )
			stableMethod();
		else
			volatileMethod();
	}
	
	@PersistentMethod( volatility=Volatility.STABLE )
	private void stableMethod() {}
	
	@PersistentMethod( volatility=Volatility.VOLATILE )
	private void volatileMethod() {}
}
