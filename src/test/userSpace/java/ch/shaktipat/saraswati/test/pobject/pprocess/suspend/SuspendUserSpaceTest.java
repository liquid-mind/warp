package ch.shaktipat.saraswati.test.pobject.pprocess.suspend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.pobject.PProcessHandle;


public class SuspendUserSpaceTest
{
	public static final String SUCCESS = "SUCCESS";
	
	private static PEnvironment pEnvironment = PEnvironmentFactory.createLocal();

	public void testNormalCase()
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new SuspendableCallable( SuspendTestVariant.TEST_INFINITE_LOOP ) );
		pProcessHandle.start();
		pProcessHandle.suspend();
		waitForState( pProcessHandle, PersistentProcessStateMachine.SUSPENDED_STATE );
		pProcessHandle.resume();
		waitForState( pProcessHandle, PersistentProcessStateMachine.ANIMATED_STATE );
		pProcessHandle.cancel();
		pProcessHandle.join();
		pProcessHandle.destroy();
	}
	
	public void testMultipleSuspendResume()
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new SuspendableCallable( SuspendTestVariant.TEST_INFINITE_LOOP ) );
		pProcessHandle.start();
		pProcessHandle.suspend();
		waitForState( pProcessHandle, PersistentProcessStateMachine.SUSPENDED_STATE );
		pProcessHandle.suspend();
		pProcessHandle.resume();
		waitForState( pProcessHandle, PersistentProcessStateMachine.ANIMATED_STATE );
		pProcessHandle.resume();
		pProcessHandle.cancel();
		pProcessHandle.join();
		pProcessHandle.destroy();
	}
	
	public void testSendEventInSuspendedState() throws InterruptedException, ExecutionException
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new SuspendableCallable( SuspendTestVariant.TEST_LISTEN ) );
		pProcessHandle.start();
		waitForState( pProcessHandle, PersistentProcessStateMachine.LISTENING_STATE );
		pProcessHandle.suspend();
		pProcessHandle.send( new SuspendEvent() );
		Thread.sleep( 1000 );
		pProcessHandle.resume();
		Future< String > future = pProcessHandle.getFuture();
		String retVal = future.get();
		pProcessHandle.destroy();
		
		assertNotNull( retVal );
		assertEquals( "success", retVal );
	}
	
	public void testCancelInSuspendedState() throws InterruptedException, ExecutionException
	{
		PProcessHandle pProcessHandle = pEnvironment.getPProcessManager().create( new SuspendableCallable( SuspendTestVariant.TEST_INFINITE_LOOP ) );
		pProcessHandle.start();
		pProcessHandle.suspend();
		waitForState( pProcessHandle, PersistentProcessStateMachine.SUSPENDED_STATE );
		pProcessHandle.cancel();
		pProcessHandle.join();
		pProcessHandle.destroy();
	}
	
	private void waitForState( PProcessHandle pProcessHandle, String state )
	{
		while ( !pProcessHandle.isInState( state ) )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch ( InterruptedException e )
			{
			}
		}
	}
}
