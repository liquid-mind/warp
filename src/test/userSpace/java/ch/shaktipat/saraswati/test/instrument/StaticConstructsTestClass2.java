package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class StaticConstructsTestClass2 implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static int staticValue;
	
	// We are testing to make sure that this static initializer
	// is actually invoked as a persistent method through the engine.
	static
	{
		staticValue = 43;
	}
	
	public static int getStaticValue()
	{
		return staticValue;
	}
}
