package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class PrimitivesTestClassProxy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int testInt( int i )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testInt( i );
	}

	public short testShort( short s )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testShort( s );
	}

	public byte testByte( byte b )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testByte( b );
	}

	public long testLong( long l )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testLong( l );
	}

	public char testChar( char c )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testChar( c );
	}

	public boolean testBoolean( boolean b )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testBoolean( b );
	}

	public float testFloat( float f )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testFloat( f );
	}

	public double testDouble( double d )
	{
		PrimitivesTestClass testClass = new PrimitivesTestClass();
		return testClass.testDouble( d );
	}
}
