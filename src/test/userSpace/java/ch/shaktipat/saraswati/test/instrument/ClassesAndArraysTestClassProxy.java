package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class ClassesAndArraysTestClassProxy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String testClass( String s )
	{
		ClassesAndArraysTestClass testClass = new ClassesAndArraysTestClass();
		return testClass.testClass( s );
	}

	public String[] testArray( String[] sa )
	{
		ClassesAndArraysTestClass testClass = new ClassesAndArraysTestClass();
		return testClass.testArray( sa );
	}
}
