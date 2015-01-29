package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class JavaThreadPersistentEventQueueEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Before
	public void setup() { invokeTestMethod(); }
	
	@After
	public void teardown() { invokeTestMethod(); }
	
	@Test
	public void testSendBeforeListen() { invokeTestMethod(); }
	
	@Test
	public void testListenBeforeSend() { invokeTestMethod(); }
	
	@Test
	public void testTryListen() { invokeTestMethod(); }
	
	@Test
	public void testFilter() { invokeTestMethod(); }
	
	@Test
	public void testOrder() { invokeTestMethod(); }
	
	@Test
	public void testFilterAndOrder() { invokeTestMethod(); }
	
	@Test
	public void testListenWithDeadlines() { invokeTestMethod(); }
	
	@Test
	public void testListenWithTimeAndTimeunit() { invokeTestMethod(); }
	
	@Test
	public void testCoverage() { invokeTestMethod(); }
}
