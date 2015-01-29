package ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace;

import java.io.Serializable;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;

@PersistentClass
public class StackTraceTestCallable implements Callable< StackTraceElement[] >, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private StackTraceTestVariant stackTraceTestVariant;

	public StackTraceTestCallable( StackTraceTestVariant stackTraceTestVariant )
	{
		super();
		this.stackTraceTestVariant = stackTraceTestVariant;
	}

	@Override
	public StackTraceElement[] call() throws Exception
	{
		StackTraceElement[] stackTrace = null;
		
		if ( stackTraceTestVariant.equals( StackTraceTestVariant.SIMPLE_TEST ) )
			stackTrace = simpleTest();
		else if ( stackTraceTestVariant.equals( StackTraceTestVariant.STATIC_TEST ) )
			stackTrace = staticTest();
		else if ( stackTraceTestVariant.equals( StackTraceTestVariant.CONSTRUCTOR_TEST ) )
			stackTrace = constructorTest();
		else if ( stackTraceTestVariant.equals( StackTraceTestVariant.PEXCEPTION_TEST ) )
			exceptionTest();
		else
			throw new IllegalStateException( "Unexpected value for stackTraceTestVariant: " + stackTraceTestVariant );
			
		return stackTrace;
	}
	
	private StackTraceElement[] simpleTest()
	{
		return simpleTest2();
	}
	
	private StackTraceElement[] simpleTest2()
	{
		return simpleTest3();
	}
	
	private StackTraceElement[] simpleTest3()
	{
		return PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess().getStackTrace();
	}
	
	private StackTraceElement[] staticTest()
	{
		return TestClass.getStackTraceStatic();
	}
	
	private StackTraceElement[] constructorTest()
	{
		TestClass testClass = new TestClass();
		
		return testClass.getStackTrace();
	}
	
	private void exceptionTest() throws Exception
	{
		throw new Exception();
	}
}
