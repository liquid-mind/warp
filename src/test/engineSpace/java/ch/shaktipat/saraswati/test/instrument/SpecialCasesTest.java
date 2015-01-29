package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

/*
 * Note that I am using parameters here simply as a way of
 * verifying that the methods executing properly. For this 
 * reason I am not checking boundary conditions (e.g.,
 * Integer.MIN_VALUE / Integer.MAX_VALUE) as I have already
 * tested that in the primitives tests.
 */
public class SpecialCasesTest extends AbstractPersistentInvocationBaseTest
{
	public SpecialCasesTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.SpecialCasesTestClass" );
	}

	@Test
	public void testRecursiveCall() throws Throwable
	{
		Object result = invokePersistentMethod( "testRecursiveCall", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 10 );
	}

	@Test
	public void testCallToSuper() throws Throwable
	{
		Object result = invokePersistentMethod( "testCallToSuper", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}
}
