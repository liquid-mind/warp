package ch.shaktipat.saraswati.test.pobject.pprocess.suspend;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class SuspendEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Test
	public void testNormalCase() { invokeTestMethod(); }
	
	@Test
	public void testMultipleSuspendResume() { invokeTestMethod(); }
	
	@Test
	public void testSendEventInSuspendedState() { invokeTestMethod(); }
	
	@Test
	public void testCancelInSuspendedState() { invokeTestMethod(); }
}
