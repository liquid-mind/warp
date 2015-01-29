package ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;

@PersistentClass
public class TestClass
{
	private static StackTraceElement[] stackTraceStatic;
	private StackTraceElement[] stackTrace;
	
	static
	{
		stackTraceStatic = PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess().getStackTrace();
	}
	
	public TestClass()
	{
		stackTrace = PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess().getStackTrace();
	}

	public static StackTraceElement[] getStackTraceStatic()
	{
		return stackTraceStatic;
	}

	public StackTraceElement[] getStackTrace()
	{
		return stackTrace;
	}
}
