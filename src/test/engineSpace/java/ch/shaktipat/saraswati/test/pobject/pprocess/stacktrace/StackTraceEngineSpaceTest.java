package ch.shaktipat.saraswati.test.pobject.pprocess.stacktrace;

import org.junit.After;
import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class StackTraceEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@After
	public void teardown() { invokeTestMethod(); }
	
	@Test
	public void testSimple() { invokeTestMethod(); }
	
	@Test
	public void testStatic() { invokeTestMethod(); }
	
	@Test
	public void testConstructor() { invokeTestMethod(); }
//	
//	@Test
//	public void testException() { invokeTestMethod(); }
}
