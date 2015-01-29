package ch.shaktipat.saraswati.test.pobject.pprocess.join;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class JoinEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Test
	public void testJoinSubscribeBeforePublish() { invokeTestMethod(); }
	
	@Test
	public void testJoinPublishBeforeSubscribe() { invokeTestMethod(); }
	
	@Test
	public void testJoinWithTimeUnit() { invokeTestMethod(); }
	
	@Test
	public void testJoinWithTimeUnitAndTimeout() { invokeTestMethod(); }
	
	@Test
	public void testJoinWithDeadline() { invokeTestMethod(); }
	
	@Test
	public void testJoinWithDeadlineAndTimeout() { invokeTestMethod(); }
	
//	@Test
//	public void testJoinInProcess() { invokeTestMethod(); }
}
