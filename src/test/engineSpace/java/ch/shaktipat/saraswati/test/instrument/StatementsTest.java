package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class StatementsTest extends AbstractPersistentInvocationBaseTest
{
	public StatementsTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.StatementsTestClass" );
	}

	@Test
	public void testInstrumentedCallInIfBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInIfBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInElseBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInElseBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInForBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInForBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 11 );
	}

	@Test
	public void testInstrumentedCallInWhileBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInWhileBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 11 );
	}

	@Test
	public void testInstrumentedCallInDoWhileBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInDoWhileBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 11 );
	}

	@Test
	public void testInstrumentedCallInTryBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInTryBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInCatchBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInCatchBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInFinallyBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInFinallyBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInCaseBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInCaseBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInDefaultBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInDefaultBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInCaseAndDefaultBlock() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInCaseAndDefaultBlock", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 3 );
	}

	@Test
	public void testInstrumentedCallInCaseBlockWithBreak() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInCaseBlockWithBreak", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 2 );
	}

	@Test
	public void testInstrumentedCallInMultipleCaseBlocks() throws Throwable
	{
		Object result = invokePersistentMethod( "testInstrumentedCallInMultipleCaseBlocks", new Class< ? >[] { int.class }, new Object[] { 1 } );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( Integer.class ) );
		assertTrue( (Integer)result == 3 );
	}
}
