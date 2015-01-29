package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class PrimitivesTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	public int testInt( int i )
	{
		return i + 1;
	}

	public short testShort( short s )
	{
		return (short)( s + 1 );
	}

	public byte testByte( byte b )
	{
		return (byte)( b + 1 );
	}

	public long testLong( long l )
	{
		return ( l + 1 );
	}

	public char testChar( char c )
	{
		return (char)( c + 1 );
	}

	public boolean testBoolean( boolean b )
	{
		return ( b ^ true );
	}

	public float testFloat( float f )
	{
		return f + 1.0F;
	}

	public double testDouble( double d )
	{
		return d + 1.0D;
	}
}
