package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class ClassesAndArraysTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String testClass( String s )
	{
		return s + " Polo";
	}

	public String[] testArray( String[] sa )
	{
		String[] sa2 = new String[ 2 ];

		sa2[ 0 ] = sa[ 0 ];
		sa2[ 1 ] = "Polo";

		return sa2;
	}
	
	// TODO: add a multi-dimensional array test.
}
