package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class InterfaceTestClass implements TestInterface, Serializable
{
	private static final long serialVersionUID = 1L;

	public int invokeInterface( int i )
	{
		TestInterface testInterface = this;
		return testInterface.interfaceMethod( i );
	}

	@Override
	public int interfaceMethod( int i )
	{
		return i + 1;
	}
}
