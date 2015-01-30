package ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.pobject.PProcessHandle;

public class StackTraceUserSpaceTest
{
	// Note that shutting down the engine after every test is necessary to avoid
	// an execution order dependency between testStatic() and testConstructor():
	// if the same engine/classloader is used for both tests then testStatic() will
	// fail if JUnit happens to invoke testConstructor() first (in that case, the
	// static initializer will be invoked as a result of StackTraceTestCallable.
	// constructorTest() being invoked rather than StackTraceTestCallable.staticTest()
	// resulting in a different stack trace).
	public void teardown()
	{
		PEngine.shutdown();
	}
	
	public void testSimple() throws InterruptedException, ExecutionException
	{
		StackTraceElement[] expectedStackTrace = new StackTraceElement[] {
				createStackTraceElement( StackTraceTestCallable.class, "simpleTest3", 53 ),
				createStackTraceElement( StackTraceTestCallable.class, "simpleTest2", 48 ),
				createStackTraceElement( StackTraceTestCallable.class, "simpleTest", 43 ),
				createStackTraceElement( StackTraceTestCallable.class, "call", 28 ),
			};
			
		StackTraceElement[] actualStackTrace = doVariant( StackTraceTestVariant.SIMPLE_TEST );
		
		validateStackTracesEqual( expectedStackTrace, actualStackTrace );
	}
	
	public void testStatic() throws InterruptedException, ExecutionException
	{
		// Note that we have to create the StackTraceElement for TestClass manually
		// since we have to defer loading to persistent process for the test to work.
		StackTraceElement[] expectedStackTrace = new StackTraceElement[] {
				new StackTraceElement( "ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace.TestClass", "<clinit>", "TestClass.java", 14 ),
				createStackTraceElement( StackTraceTestCallable.class, "staticTest", 58 ),
				createStackTraceElement( StackTraceTestCallable.class, "call", 30 ),
			};

		StackTraceElement[] actualStackTrace = doVariant( StackTraceTestVariant.STATIC_TEST );
		
		validateStackTracesEqual( expectedStackTrace, actualStackTrace );
	}
	
	public void testConstructor() throws InterruptedException, ExecutionException
	{
		StackTraceElement[] expectedStackTrace = new StackTraceElement[] {
				new StackTraceElement( "ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace.TestClass", "<init>", "TestClass.java", 19 ),
				createStackTraceElement( StackTraceTestCallable.class, "constructorTest", 65 ),
				createStackTraceElement( StackTraceTestCallable.class, "call", 32 ),
			};

		StackTraceElement[] actualStackTrace = doVariant( StackTraceTestVariant.CONSTRUCTOR_TEST );
		
		validateStackTracesEqual( expectedStackTrace, actualStackTrace );
	}
	
	@SuppressWarnings("deprecation")
	public void testException() throws InterruptedException, ExecutionException
	{
		StackTraceElement[] expectedStackTrace = new StackTraceElement[] {
				createStackTraceElement( StackTraceTestCallable.class, "exceptionTest", 70 ),
				createStackTraceElement( StackTraceTestCallable.class, "call", 34 ),
			};

		StackTraceElement[] actualStackTrace = null;
		
		try
		{
			doVariant( StackTraceTestVariant.PEXCEPTION_TEST );
		}
		catch ( ExecutionException e )
		{
			assertTrue( e.getCause() instanceof Exception );
			
			Exception c = (Exception)e.getCause();
			actualStackTrace = c.getStackTrace();
		}
		
		validateStackTracesEqual( expectedStackTrace, actualStackTrace );
	}
	
	private StackTraceElement[] doVariant( StackTraceTestVariant variant ) throws InterruptedException, ExecutionException
	{
		Callable< StackTraceElement[] > callable = new StackTraceTestCallable( variant );
		PProcessHandle handle = PEnvironmentFactory.createLocal().getPProcessManager().create( callable );
		handle.start();
		Future< StackTraceElement[] > future = handle.getFuture();
		
		return future.get();
	}
	
	private static StackTraceElement createStackTraceElement( Class< ? > theClass, String methodName, int lineNumber )
	{
		return new StackTraceElement( theClass.getName(), methodName, theClass.getSimpleName() + ".java", lineNumber );
	}

	@SuppressWarnings("deprecation")
	private static void validateStackTracesEqual( StackTraceElement[] expectedStackTrace, StackTraceElement[] actualStackTrace )
	{
		assertNotNull( expectedStackTrace );
		assertNotNull( actualStackTrace );
		assertEquals( expectedStackTrace.length, actualStackTrace.length );
		
		for ( int i = 0 ; i < expectedStackTrace.length ; ++i )
			assertEquals( expectedStackTrace[ i ],  actualStackTrace[ i ] );
	}
}
