package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class PersistentProcessPersistentEventQueueEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Test
	public void testSendBeforeListen() { invokeTestMethod(); }
	
	@Test
	public void testListenBeforeSend() { invokeTestMethod(); }
	
	@Test
	public void testListenWithDeadlines() { invokeTestMethod(); }
	
	@Test
	public void testCoverage() { invokeTestMethod(); }
}
