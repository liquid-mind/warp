package ch.shaktipat.saraswati.test.instrument.algorithms;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class QuicksortTest extends AbstractPersistentInvocationBaseTest
{
	private int[] numbers;
	private final static int SIZE = 7;
	private final static int MAX = 20;

	public QuicksortTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.algorithms.Quicksort" );
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
	public void testNull() throws Throwable
	{
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { null } );
	}

	@Test
	public void testEmpty() throws Throwable
	{
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { new int[ 0 ] } );
	}

	@Test
	public void testSimpleElement() throws Throwable
	{
		int[] test = new int[ 1 ];
		test[ 0 ] = 5;
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { test } );
	}

	@Test
	public void testSpecial() throws Throwable
	{
		int[] test = { 5, 5, 6, 6, 4, 4, 5, 5, 4, 4, 6, 6, 5, 5 };
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { test } );
		if ( !validate( test ) )
		{
			fail( "Should not happen" );
		}
	}

	@Test
	public void testQuickSort() throws Throwable
	{
		invokePersistentMethod( "sort", new Class< ? >[] { int[].class }, new Object[] { numbers } );

		if ( !validate( numbers ) )
		{
			fail( "Should not happen" );
		}
	}

	private boolean validate( int[] numbers )
	{
		for ( int i = 0; i < ( numbers.length - 1 ); i++ )
		{
			if ( numbers[ i ] > numbers[ i + 1 ] )
			{
				return false;
			}
		}
		return true;
	}
}
