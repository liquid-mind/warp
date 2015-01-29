package ch.shaktipat.saraswati.test.instrument.algorithms;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class MergesortTest extends AbstractPersistentInvocationBaseTest
{
	private int[] numbers;
	private final static int SIZE = 7;
	private final static int MAX = 20;

	public MergesortTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.algorithms.Mergesort" );
	}

	@Before
	public void setUp() throws Exception
	{
		numbers = new int[ SIZE ];
		Random generator = new Random();
		for ( int i = 0; i < numbers.length; i++ )
		{
			numbers[ i ] = generator.nextInt( MAX );
		}
	}

	@Test
	public void testMergeSort() throws Throwable
	{
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { numbers } );

		for ( int i = 0; i < ( numbers.length - 1 ); i++ )
		{
			if ( numbers[ i ] > numbers[ i + 1 ] )
			{
				fail( "Should not happen" );
			}
		}
	}

	@Test
	public void itWorksRepeatably() throws Throwable
	{
		for ( int i = 0; i < 200; i++ )
		{
			numbers = new int[ SIZE ];
			Random generator = new Random();
			for ( int a = 0; a < numbers.length; a++ )
			{
				numbers[ a ] = generator.nextInt( MAX );
			}
			
			invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { numbers } );
	
			for ( int j = 0; j < ( numbers.length - 1 ); j++ )
			{
				if ( numbers[ j ] > numbers[ j + 1 ] )
				{
					fail( "Should not happen" );
				}
			}
		}
	}
}
