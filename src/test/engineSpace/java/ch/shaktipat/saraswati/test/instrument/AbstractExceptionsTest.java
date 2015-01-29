package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public abstract class AbstractExceptionsTest extends AbstractPersistentInvocationBaseTest
{
	public AbstractExceptionsTest( String testClassName )
	{
		super( testClassName );
	}
	
	private static final String TEST_EXCEPTION = "ch.shaktipat.saraswati.test.instrument.TestException";
	
	@Test
	public void testUncaughtException()
	{
		try
		{
			invokePersistentMethod( "testUncaughtException", new Class< ? >[] {}, new Object[] {} );
		}
		catch ( Throwable t )
		{
			assertTrue( t != null );
			assertTrue( t.getClass().getName().equals( TEST_EXCEPTION ) );
			assertTrue( t.getMessage().equals( "Test Exception" ) );
		}
	}

	@Test
	public void testCaughtException() throws Throwable
	{
		Object result = invokePersistentMethod( "testCaughtException", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().getName().equals( TEST_EXCEPTION ) );
	}

	@Test
	public void testMultipleInvocationsInTryBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testMultipleInvocationsInTryBlock", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( ((int)result) == 2 );
	}

	@Test
	public void testMultipleInvocationsInMultipleTryBlocks() throws Throwable
	{
		Object result = invokePersistentMethod( "testMultipleInvocationsInMultipleTryBlocks", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( ((int)result) == 2 );
	}
}
