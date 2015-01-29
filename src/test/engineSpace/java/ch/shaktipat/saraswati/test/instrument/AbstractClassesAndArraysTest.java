package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public abstract class AbstractClassesAndArraysTest extends AbstractPersistentInvocationBaseTest
{
	public AbstractClassesAndArraysTest( String testClassName )
	{
		super( testClassName );
	}

	@Test
	public void testClass() throws Throwable
	{
		Object result = invokePersistentMethod( "testClass", new Class< ? >[] { String.class }, new Object[] { "Marco" } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( result.equals( "Marco Polo" ) );
	}

	@Test
	public void testArrays() throws Throwable
	{
		Object result = invokePersistentMethod( "testArray", new Class< ? >[] { String[].class }, new Object[] { (Object)new String[] { "Marco" } } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String[].class ) );
		String[] retArray = (String[])result;
		assertTrue( retArray[ 0 ].equals( "Marco" ) );
		assertTrue( retArray[ 1 ].equals( "Polo" ) );		
	}
}
