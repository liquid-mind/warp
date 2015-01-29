package ch.shaktipat.saraswati.internal.test;

import java.util.ArrayList;
import java.util.List;

import ch.shaktipat.saraswati.internal.common.DebugConstants;

public class ConcurrentTestingHelper
{
	public static final String PERSISTENT_EVENT_QUEUE_USER_SPACE_TEST_CLASS_NAME = "PersistentEventQueueUserSpaceTest";
	public static final String LISTEN_BEFORE_SEND_TEST_NAME = PERSISTENT_EVENT_QUEUE_USER_SPACE_TEST_CLASS_NAME + ".testListenBeforeSend()";
	public static final String LISTEN_BEFORE_SEND_LISTENING_CONDITION = "listening";

	public static final String JOIN_USER_SPACE_TEST_CLASS_NAME = "JoinUserSpaceTest";
	public static final String JOIN_SUBSCRIBE_BEFORE_PUBLISH_TEST_NAME = PERSISTENT_EVENT_QUEUE_USER_SPACE_TEST_CLASS_NAME + ".testJoinSubscribeBeforePublish()";
	public static final String JOIN_SUBSCRIBE_BEFORE_PUBLISH_SUBSCRIBED_CONDITION = "subscribed";
	
	public static final String JOIN_PUBLISH_BEFORE_SUBSCRIBE_TEST_NAME = PERSISTENT_EVENT_QUEUE_USER_SPACE_TEST_CLASS_NAME + ".testJoinPublishBeforeSubscribe()";
	public static final String JOIN_PUBLISH_BEFORE_SUBSCRIBE_PUBLISHED_CONDITION = "published";

	private static String testName;
	private static List< TestStage > testStages;
	
	public static void setupTest( String testName, String ... testStageNames )
	{
		ConcurrentTestingHelper.testName = testName;
		testStages = new ArrayList< TestStage >();
		
		for ( String testStageName : testStageNames )
			testStages.add( new TestStage( testStageName ) );
	}
	
	public static void clearTest()
	{
		testName = null;
		testStages = null;
	}
	
	public static void signal( String testName, String testStageName )
	{
		if ( !DebugConstants.CONCURRENT_TESTING_DEBUG )
			return;
		
		if ( testName == null || !testName.equals( ConcurrentTestingHelper.testName ) )
			return;
		
		for ( TestStage testStage : testStages )
		{
			String name = testStage.getName();
			boolean testStageReached = testStage.testStageReached();
			
			if ( name.equals( testStageName ) && testStageReached )
				throw new IllegalStateException( "Thread tried to signal a test stage that was already reached: name=" + name );
			else if ( !name.equals( testStageName ) && !testStageReached )
				throw new IllegalStateException( "Thread tried to signal a test stage before a prior stage was reached: name=" + name );
			else if ( name.equals( testStageName ) )
			{
				testStage.signal();
				break;
			}
		}
	}
	
	public static void await( String testName, String testStageName )
	{
		if ( !DebugConstants.CONCURRENT_TESTING_DEBUG )
			return;
		
		if ( testName == null || !testName.equals( ConcurrentTestingHelper.testName ) )
			return;
		
		boolean testStageFound = false;
		
		for ( TestStage testStage : testStages )
		{
			if ( testStage.getName().equals( testStageName ) )
			{
				testStageFound = true;
				testStage.await();
				break;
			}
		}
		
		if ( !testStageFound )
			throw new RuntimeException( "Unable to find specified test stage: name=" + testStageName );
	}
}
