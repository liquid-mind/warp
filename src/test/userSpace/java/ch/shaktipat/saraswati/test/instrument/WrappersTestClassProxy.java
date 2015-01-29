package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class WrappersTestClassProxy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public Integer testInteger( Integer i )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testInteger( i );
	}

	public Short testShort( Short s )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testShort( s );
	}

	public Byte testByte( Byte b )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testByte( b );
	}

	public Long testLong( Long l )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testLong( l );
	}

	public Character testCharacter( Character c )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testCharacter( c );
	}

	public Boolean testBoolean( Boolean b )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testBoolean( b );
	}

	public Float testFloat( Float f )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testFloat( f );
	}

	public Double testDouble( Double d )
	{
		WrappersTestClass testClass = new WrappersTestClass();
		return testClass.testDouble( d );
	}
}
