package ch.shaktipat.saraswati.test.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class TestObject implements TestInterface1, TestInterface2, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public void method5( List< String > someList )
	{
	}

	@Override
	public void method1()
	{
	}

	@Override
	public String method2( int someArg, Date[] someOtherArg )
	{
		return null;
	}

	@Override
	public void method3( String... strings )
	{
	}

	@Override
	public void method4() throws RuntimeException, IllegalStateException
	{
	}
}
