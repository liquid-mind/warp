package ch.shaktipat.saraswati.test.web;

import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.PProcessEvent;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PEventQueueManager;
import ch.shaktipat.saraswati.pobject.manager.PEventTopicManager;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.pobject.manager.PSharedObjectManager;
import ch.shaktipat.saraswati.volatility.Volatility;

public class TestDataCreator
{
	private static PProcessManager processManager;
	private static PEventQueueManager eventQueueManager;
	private static PEventTopicManager eventTopicManager;
	private static PSharedObjectManager sharedObjectManager;
	
	public static void main( String[] args )
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		processManager = env.getPProcessManager();
		eventQueueManager = env.getPEventQueueManager();
		eventTopicManager = env.getPEventTopicManager();
		sharedObjectManager = env.getPSharedObjectManager();
		
		createDoNothingProcessUnstarted();
		createDoNothingProcessStarted();
		createThrowExceptionProcess();
		createListenProcess();
		createLongNameProcess();
		createTopicWithSubscriptions();
		createSharedObject();
	}
	
	private static void createDoNothingProcessUnstarted()
	{
		processManager.create( new TestRunnable( TestRunnable.DO_NOTHING ), "Do Nothing Process: Unstarted", Volatility.DYNAMIC, null, false, false );
	}
	
	private static void createDoNothingProcessStarted()
	{
		PProcessHandle handle = processManager.create( new TestRunnable( TestRunnable.DO_NOTHING ), "Do Nothing Process: Started", Volatility.DYNAMIC, null, false, false );
		handle.start();
		handle.join();
	}
	
	private static void createThrowExceptionProcess()
	{
		PProcessHandle handle = processManager.create( new TestRunnable( TestRunnable.THROW_EXCEPTION ), "Throw Exception Process", Volatility.DYNAMIC, null, false, false );
		handle.start();
		handle.join();
	}
	
	private static void createListenProcess()
	{
		PProcessHandle handle = processManager.create( new TestRunnable( TestRunnable.LISTEN_FOR_EVENT ), "Listen Process", Volatility.DYNAMIC, null, false, false );
		handle.start();
		handle.send( new TestEvent() );
		handle.send( new TestEvent() );
		handle.send( new TestEvent() );

		// TODO Significant issues arise when the processes are still running at the time
		// that the VM starts closing down. I got a number of very strange errors that made
		// no sense at first glance, e.g. an exception due to a PersistentProcessImpl invoking
		// a non-existant method in StringBuilder (?!). Sooner or later we have to figure out
		// how to shutdown the VM gracefully, even when processes are running.
		
		PEventQueue queue = eventQueueManager.create();
		String topicfilter = "event.getProperty( '" + PProcessEvent.ENTERED_PROCESS_STATE + "' ).equals( '" + PProcessEvent.LISTENING_STATE +
				"' ) && event.getSenderOID().toString().equals( '" + handle.getOID().toString() + "' )";
		handle.subscribe( queue.getOID(), topicfilter, true );
		queue.listen();
		queue.destroy();
	}
	
	private static void createLongNameProcess()
	{
		PProcessHandle handle4 = processManager.create( new TestRunnable( TestRunnable.DO_NOTHING ), "Process with ridiculously, very, very, very, very long name", Volatility.DYNAMIC, null, false, false );
		handle4.start();
		handle4.join();
	}
	
	private static void createTopicWithSubscriptions()
	{
		PEventTopic pEventTopic = eventTopicManager.create( "Topic with subscriptions", Scope.GLOBAL );
		PEventQueue pEventQueue = eventQueueManager.create( "Subscribing Queue", Scope.GLOBAL );
		
		pEventTopic.subscribe( pEventQueue.getOID(), "event.getProperty( 'My Property' ).equals( 'My Value' )", false );
		pEventTopic.subscribe( pEventQueue.getOID(), "event.getProperty( 'My Property 2' ).equals( 'My Value 2' ) && event.getSenderOID().toString().equals( '1ed08819-f146-41a9-802e-aa746dc9d2ad' )", true );
	}
	
	private static void createSharedObject()
	{
		sharedObjectManager.create( "Some Shared Object", new TestObject() );
	}
}
