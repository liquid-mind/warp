package ch.shaktipat.saraswati.test.pobject.pprocess.future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;

public class FutureUserSpaceTest
{
	public void testGet() throws InterruptedException, ExecutionException
	{
		PProcessHandle handle = createAndStartProcess( "marco" );
		Future< String > future = handle.getFuture();
		String msg = future.get();
		handle.destroy();
		
		assertNotNull( msg );
		assertEquals( "marco polo!", msg );
	}
	
	public void testGetWithTimeout() throws InterruptedException, ExecutionException, TimeoutException
	{
		PProcessHandle handle = createAndStartProcess( "marco" );
		Future< String > future = handle.getFuture();
		String msg = future.get( 1, TimeUnit.DAYS );
		handle.destroy();
		
		assertNotNull( msg );
		assertEquals( "marco polo!", msg );
	}
	
	public void testGetWithException() throws InterruptedException, TimeoutException
	{
		PProcessHandle handle = createAndStartProcess( null );
		Future< String > future = handle.getFuture();
		boolean caughtException = false;
		
		try
		{
			future.get();
		}
		catch ( ExecutionException e )
		{
			caughtException = true;
			
			assertNotNull( e.getCause() );
			assertTrue( e.getCause() instanceof FutureTestException );
		}
		
		if ( !caughtException )
			throw new RuntimeException( "Did not catch expected exception: " + FutureTestException.class.getName() );
	}
	
	private PProcessHandle createAndStartProcess( String msg )
	{
		Callable< String > callable = new FutureTestCallable( msg );
		PProcessHandle handle = PEnvironmentFactory.createLocal().getPProcessManager().create( callable );
		handle.start();
		
		return handle;
	}
}
