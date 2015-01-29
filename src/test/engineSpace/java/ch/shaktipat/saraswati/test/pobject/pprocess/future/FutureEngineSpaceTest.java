package ch.shaktipat.saraswati.test.pobject.pprocess.future;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class FutureEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Test
	public void testGet() { invokeTestMethod(); }
	
	@Test
	public void testGetWithTimeout() { invokeTestMethod(); }
	
	@Test
	public void testGetWithException() { invokeTestMethod(); }
}
