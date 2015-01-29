package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class StatementsTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int testInstrumentedCallInIfBlock( int i )
	{
		if ( getTrue() )
		{
			i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInElseBlock( int i )
	{
		if ( !getTrue() )
		{
			i -= 1;
		}
		else
		{
			i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInForBlock( int i )
	{
		for ( int j = 0; j < 10; ++j )
		{
			i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInWhileBlock( int i )
	{
		int j = 0;

		while ( j < 10 )
		{
			i = incByOne( i );
			++j;
		}

		return i;
	}

	public int testInstrumentedCallInDoWhileBlock( int i )
	{
		int j = 0;

		do
		{
			i = incByOne( i );
			++j;
		} while ( j < 10 );

		return i;
	}

	public int testInstrumentedCallInTryBlock( int i )
	{
		try
		{
			i = incByOne( i );
		}
		catch ( Throwable t )
		{}

		return i;
	}

	public int testInstrumentedCallInCatchBlock( int i )
	{
		try
		{
			throw new RuntimeException();
		}
		catch ( Throwable t )
		{
			i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInFinallyBlock( int i )
	{
		try
		{}
		catch ( Throwable t )
		{}
		finally
		{
			i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInCaseBlock( int i )
	{
		switch ( i ) {
			case 1:
				i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInDefaultBlock( int i )
	{
		switch ( i ) {
			default:
				i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInCaseAndDefaultBlock( int i )
	{
		switch ( i ) {
			case 1:
				i = incByOne( i );
			default:
				i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInCaseBlockWithBreak( int i )
	{
		switch ( i ) {
			case 1:
				i = incByOne( i );
				break;
			default:
				i = incByOne( i );
		}

		return i;
	}

	public int testInstrumentedCallInMultipleCaseBlocks( int i )
	{
		// The 4-byte alignment of this first switch block
		// will cause the preamble code to be padded.
		switch ( i ) {
			case 1:
				i = incByOne( i );
		}

		// The 4-byte alignment of this second switch block
		// will cause the first instrumented invocation
		// to be padded.
		switch ( i ) {
			case 2:
				i = incByOne( i );
		}

		return i;
	}

	private int incByOne( int i )
	{
		return i + 1;
	}

	private boolean getTrue()
	{
		return true;
	}
}
