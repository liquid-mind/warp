package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class NullPointersTest extends AbstractPersistentInvocationBaseTest
{
	public NullPointersTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.NullPointersTestClass" );
	}

	@Test
	public void testNullInLocalVariable() throws Throwable
	{
		invokePersistentMethod( "testNullInLocalVariable", new Class< ? >[] {}, new Object[] {} );
	}

	@Test
	public void testNullInParameter() throws Throwable
	{
		invokePersistentMethod( "testNullInParameter", new Class< ? >[] {}, new Object[] {} );
	}

	@Test
	public void testNullInReturn() throws Throwable
	{
		invokePersistentMethod( "testNullInReturn", new Class< ? >[] {}, new Object[] {} );
	}

	@Test
	public void testNullPointerException() throws Throwable
	{
		Object result = invokePersistentMethod( "testNullPointerException", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Boolean.class ) );
		assertTrue( (boolean)result == true );
	}
}
