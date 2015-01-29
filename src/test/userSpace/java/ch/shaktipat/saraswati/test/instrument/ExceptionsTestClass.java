package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class ExceptionsTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public void testUncaughtException() throws TestException
	{
		throw new TestException( "Test Exception" );
	}

	public TestException testCaughtException() throws TestException
	{
		TestException testException = null;

		try
		{
			testUncaughtException();
		}
		catch ( TestException e )
		{
			testException = e;
		}

		return testException;
	}
	
	// This particular constellation exposed a bug in the old
	// exception handling instrumentation code where the try
	// block (represented by an exception table entry in bytecode)
	// was incorrectly split.
	public int testMultipleInvocationsInTryBlock()
	{
		int i = 0;
		
		try
		{
			i = inc( i );
			i = inc( i );
		}
		catch ( RuntimeException e )
		{
			// Although it may look like this statement is irrelevant
			// for the test, it did actually make a difference with
			// the buggy exception code, so I'm leaving it in...
			i = inc( i );
		}
		
		return i;
	}
	
	public int testMultipleInvocationsInMultipleTryBlocks()
	{
		int i = 0;
		
		try
		{
			try
			{
				i = inc( i );
				i = inc( i );
			}
			catch ( RuntimeException e )
			{
				i = inc( i );
			}
		}
		// Note that I'm using Error here just to make it easier
		// to distinguish the two try blocks in bytecode.
		catch ( Error e )
		{
			i = inc( i );
		}
		
		return i;
	}
	
	private int inc( int i )
	{
		return i + 1;
	}
}
