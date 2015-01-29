package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class SpecialCasesTestClass extends SpecialCasesTestSuperClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int testRecursiveCall( int i )
	{
		if ( i < 10 )
		{
			i = testRecursiveCall( i + 1 );
		}

		return i;
	}

	public int testCallToSuper( int i )
	{
		return incByOne( i );
	}
	
	public String testInvocationBetweenNewAndInvokeSpecial( String msg )
	{
		return new String( getString( msg ) );
	}
	
	private String getString( String msg )
	{
		return msg + "!";
	}

	public long testDoubleWordValueWithFramesAdjustedForNewOperator( long value )
	{
		return new Long( value + 1 );
	}
}
