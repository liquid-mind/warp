package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class ExceptionsTestClassProxy implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public void testUncaughtException() throws TestException
	{
		ExceptionsTestClass testClass = new ExceptionsTestClass();
		testClass.testUncaughtException();
	}

	public TestException testCaughtException() throws TestException
	{
		ExceptionsTestClass testClass = new ExceptionsTestClass();
		return testClass.testCaughtException();
	}

	public int testMultipleInvocationsInTryBlock()
	{
		ExceptionsTestClass testClass = new ExceptionsTestClass();
		return testClass.testMultipleInvocationsInTryBlock();
	}

	public int testMultipleInvocationsInMultipleTryBlocks()
	{
		ExceptionsTestClass testClass = new ExceptionsTestClass();
		return testClass.testMultipleInvocationsInMultipleTryBlocks();
	}
}
