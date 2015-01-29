package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class InterfaceTest extends AbstractPersistentInvocationBaseTest
{
	public InterfaceTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.InterfaceTestClass" );
	}

	@Test
	public void testInvokeInterface() throws Throwable
	{
		Object result = invokePersistentMethod( "invokeInterface", new Class< ? >[] { int.class }, new Object[] { (int)1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}
}
