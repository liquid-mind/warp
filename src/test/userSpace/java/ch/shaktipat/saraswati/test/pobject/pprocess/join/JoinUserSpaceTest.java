package ch.shaktipat.saraswati.test.pobject.pprocess.join;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelperProxy;
import ch.shaktipat.saraswati.test.common.TimingHelper;

public class JoinUserSpaceTest
{
	public static final String JOIN_TEST_RESULT = "JOIN_TEST_RESULT";
	public static final String SUCCESS = "SUCCESS";
	
	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();
	
	public void testJoinSubscribeBeforePublish()
	{
		ConcurrentTestingHelper.setupTest(
			ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_TEST_NAME,
			ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_SUBSCRIBED_CONDITION );

		try
		{
			testJoinSubscribeBeforePublish2();
		}
		finally
		{
			ConcurrentTestingHelper.clearTest();
		}
	}
	
	private void testJoinSubscribeBeforePublish2()
	{
		Runnable runnable = new JoinableRunnable();
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	public void testJoinPublishBeforeSubscribe()
	{
		ConcurrentTestingHelper.setupTest(
			ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_TEST_NAME,
			ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_PUBLISHED_CONDITION );
		
		try
		{
			testJoinPublishBeforeSubscribe2();
		}
		finally
		{
			ConcurrentTestingHelper.clearTest();
		}
	}
	
	public void testJoinPublishBeforeSubscribe2()
	{
		Runnable runnable = new JoinableRunnable();
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	public void testJoinWithTimeUnit()
	{
		Runnable runnable = new JoinableRunnable();
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
		handle.join( 1000, TimeUnit.MILLISECONDS );
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	public void testJoinWithTimeUnitAndTimeout()
	{
		Runnable runnable = new JoinableRunnable( 2000 );
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
	    Date deadline = getDeadline( 1 );
		handle.join( 1000, TimeUnit.MILLISECONDS );
	    Date currentTime = new Date();
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
	    assertTrue( currentTime.after( deadline ) );
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	public void testJoinWithDeadline()
	{
		Runnable runnable = new JoinableRunnable();
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
		handle.join( TimingHelper.getDeadline( 1000 ) );
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	public void testJoinWithDeadlineAndTimeout()
	{
		Runnable runnable = new JoinableRunnable( 2000 );
		PProcessHandle handle = pEnvironment.getPProcessManager().create( runnable );
		handle.start();
	    Date deadline = getDeadline( 1 );
		handle.join( TimingHelper.getDeadline( 1000 ) );
	    Date currentTime = new Date();
		handle.join();
		
		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
		
	    assertTrue( currentTime.after( deadline ) );
		assertNotNull( success );
		assertEquals( SUCCESS, success );
		
		handle.destroy();
	}
	
	private Date getDeadline( int secondsInFuture )
	{
		Calendar cal = Calendar.getInstance();
	    cal.setTime( new Date() );
	    cal.add( Calendar.SECOND, secondsInFuture );
	    Date deadline = cal.getTime();
	    
	    return deadline;
	}
	
	// TODO Although we have coverage tests of process-to-process join
	// we really need to introduce tests analogous to those above to
	// validate the key lines of execution.
//	public void testJoinInProcess()
//	{
//		Runnable runnable = new JoiningRunnable();
//		PProcessHandle handle = PEnvironment.getPProcessManager().create( runnable );
//		handle.start();
//		handle.join();
//		
//		String success = InterprocessCommunicationHelperProxy.getProperty( handle.getOID(), JOIN_TEST_RESULT );
//		
//		assertNotNull( success );
//		assertEquals( SUCCESS, success );
//		
//		handle.destroy();
//	}
}
