package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class StaticConstructsTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static int staticValue;
	
	static
	{
		staticValue = 42;
	}
	
	public int invokeStatic( int i )
	{
		return staticMethod( i );
	}

	public static int staticMethod( int i )
	{
		return i + 1;
	}
	
	public int getStaticValue()
	{
		return staticValue;
	}
	
	public int getStaticValueFromSecondClass()
	{
		return StaticConstructsTestClass2.getStaticValue();
	}
}
