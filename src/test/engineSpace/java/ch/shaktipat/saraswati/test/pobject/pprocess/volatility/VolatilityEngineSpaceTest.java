package ch.shaktipat.saraswati.test.pobject.pprocess.volatility;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractUserSpaceMethodBaseTest;

public class VolatilityEngineSpaceTest extends AbstractUserSpaceMethodBaseTest
{
	@Test
	public void testStableToStable()
	{
		invokeTestMethod();
	}
	
	@Test
	public void testVolatileToVolatile()
	{
		invokeTestMethod();
	}
	
	@Test
	public void testStableToVolatile()
	{
		invokeTestMethod();
	}
	
	// TODO: reenable these tests once Future has been implemented.
//	@Test
//	public void testVolatileToStable()
//	{
//		invokeTestMethod();
//	}
//	
//	@Test
//	public void testVolatileToStableCatchException()
//	{
//		invokeTestMethod();
//	}
}
