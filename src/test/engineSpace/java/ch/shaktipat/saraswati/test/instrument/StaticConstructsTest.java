package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class StaticConstructsTest extends AbstractPersistentInvocationBaseTest
{
	public StaticConstructsTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.StaticConstructsTestClass" );
	}

	@Test
	public void testInvokeStatic() throws Throwable
	{
		Object result = invokePersistentMethod( "invokeStatic", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testGetStaticValue() throws Throwable
	{
		Object result = invokePersistentMethod( "getStaticValue", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 42 );
	}

	@Test
	public void testGetStaticValueFromSecondClass() throws Throwable
	{
		Object result = invokePersistentMethod( "getStaticValueFromSecondClass", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 43 );
	}
}
