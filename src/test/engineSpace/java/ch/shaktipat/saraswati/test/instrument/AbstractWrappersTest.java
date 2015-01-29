package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public abstract class AbstractWrappersTest extends AbstractPersistentInvocationBaseTest
{
	public AbstractWrappersTest( String testClassName )
	{
		super( testClassName );
	}

	@Test
	public void testInteger() throws Throwable
	{
		Object result = invokePersistentMethod( "testInteger", new Class< ? >[] { Integer.class }, new Object[] { Integer.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == Integer.MIN_VALUE + 1 );
		
		result = invokePersistentMethod( "testInteger", new Class< ? >[] { Integer.class }, new Object[] { Integer.MAX_VALUE - 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == Integer.MAX_VALUE );
	}

	@Test
	public void testShort() throws Throwable
	{
		Object result = invokePersistentMethod( "testShort", new Class< ? >[] { Short.class }, new Object[] { Short.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Short.class ) );
		assertTrue( (Short)result == Short.MIN_VALUE + 1 );
		
		result = invokePersistentMethod( "testShort", new Class< ? >[] { Short.class }, new Object[] { (short)( Short.MAX_VALUE - 1 ) } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Short.class ) );
		assertTrue( (Short)result == Short.MAX_VALUE );
	}

	@Test
	public void testByte() throws Throwable
	{
		Object result = invokePersistentMethod( "testByte", new Class< ? >[] { Byte.class }, new Object[] { Byte.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Byte.class ) );
		assertTrue( (Byte)result == Byte.MIN_VALUE + 1 );
		
		result = invokePersistentMethod( "testByte", new Class< ? >[] { Byte.class }, new Object[] { (byte)( Byte.MAX_VALUE - 1 ) } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Byte.class ) );
		assertTrue( (Byte)result == Byte.MAX_VALUE );
	}

	@Test
	public void testLong() throws Throwable
	{
		Object result = invokePersistentMethod( "testLong", new Class< ? >[] { Long.class }, new Object[] { Long.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Long.class ) );
		assertTrue( (Long)result == Long.MIN_VALUE + 1 );
		
		result = invokePersistentMethod( "testLong", new Class< ? >[] { Long.class }, new Object[] { Long.MAX_VALUE - 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Long.class ) );
		assertTrue( (Long)result == Long.MAX_VALUE );
	}

	@Test
	public void testCharacter() throws Throwable
	{
		Object result = invokePersistentMethod( "testCharacter", new Class< ? >[] { Character.class }, new Object[] { Character.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Character.class ) );
		assertTrue( (Character)result == Character.MIN_VALUE + 1 );
		
		result = invokePersistentMethod( "testCharacter", new Class< ? >[] { Character.class }, new Object[] { (char)( Character.MAX_VALUE - 1 ) } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Character.class ) );
		assertTrue( (Character)result == Character.MAX_VALUE );
	}

	@Test
	public void testBoolean() throws Throwable
	{
		Object result = invokePersistentMethod( "testBoolean", new Class< ? >[] { Boolean.class }, new Object[] { false } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Boolean.class ) );
		assertTrue( (Boolean)result == true );
	}

	@Test
	public void testFloat() throws Throwable
	{
		Object result = invokePersistentMethod( "testFloat", new Class< ? >[] { Float.class }, new Object[] { Float.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Float.class ) );
		assertTrue( (Float)result == Float.MIN_VALUE + 1.0F );
		
		result = invokePersistentMethod( "testFloat", new Class< ? >[] { Float.class }, new Object[] { Float.MAX_VALUE - 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Float.class ) );
		assertTrue( (Float)result == Float.MAX_VALUE );
	}

	@Test
	public void testDouble() throws Throwable
	{
		Object result = invokePersistentMethod( "testDouble", new Class< ? >[] { Double.class }, new Object[] { Double.MIN_VALUE } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Double.class ) );
		assertTrue( (Double)result == Double.MIN_VALUE + 1.0F );
		
		result = invokePersistentMethod( "testDouble", new Class< ? >[] { Double.class }, new Object[] { Double.MAX_VALUE - 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Double.class ) );
		assertTrue( (Double)result == Double.MAX_VALUE );
	}
}
