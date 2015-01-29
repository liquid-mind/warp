package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class WrappersTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	public Integer testInteger( Integer i )
	{
		return i + 1;
	}
	
	public Short testShort( Short s )
	{
		return (short)( s + 1 );
	}

	public Byte testByte( Byte b )
	{
		return (byte)( b + 1 );
	}

	public Long testLong( Long l )
	{
		return (long)( l + 1 );
	}

	public Character testCharacter( Character c )
	{
		return (char)( c + 1 );
	}

	public Boolean testBoolean( Boolean b )
	{
		return (boolean)( b ^ true );
	}

	public Float testFloat( Float f )
	{
		return f + 1.0F;
	}

	public Double testDouble( Double d )
	{
		return d + 1.0D;
	}
}
