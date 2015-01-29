package ch.shaktipat.saraswati.test.pobject.pprocess.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import ch.shaktipat.exwrapper.java.lang.ClassLoaderWrapper;
import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.ConstructorWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.MethodWrapper;
import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;


public class PersistUserSpaceTest
{
	public static final String PERSIST_TEST_RESULT = "PERSIST_TEST_RESULT";
	
	public void testPersistence()
	{
		Runnable runnable = new PersistTestRunnable( "marco" );
		PProcessHandle handle = PEnvironmentFactory.createLocal().getPProcessManager().create( runnable );
		handle.start();
		ThreadWrapper.sleep( 1000 );
		PEngine.shutdown();
		PEngine.startup();
		handle.send( getEventInCurrentClassLoader() );
		handle.join();
		handle.destroy();
		
		String msg = getMsgInCurrentClassLoader( handle.getOID() );
		
		assertNotNull( msg );
		assertEquals( "marco pollo!", msg );
	}
	
	// We are doing this because HelloEvent has already
	// been loaded with the classloader associated with the
	// engine before shutdown. But after the second startup, we
	// need a new HelloEvent that is associated with the new
	// class loader.
	// Note that this is not something that could actually occur in
	// normal usage, as the engine normally runs continuously
	// until the main method completes.
	private static Event getEventInCurrentClassLoader()
	{
		Class< ? > theClass = ClassLoaderWrapper.loadClass( PEngine.getPEngine().getPClassLoader(), PersistTestEvent.class.getName() );
		Constructor< ? > constructor = ClassWrapper.getDeclaredConstructor( theClass, String.class );
		Event helloEvent = (Event)ConstructorWrapper.newInstance( constructor, " pollo!" );
		
		return helloEvent;
	}
	
	private static String getMsgInCurrentClassLoader( PProcessOID processOID )
	{
		Class< ? > theClass = ClassLoaderWrapper.loadClass( PEngine.getPEngine().getPClassLoader(), InterprocessCommunicationHelper.class.getName() );
		Method method = ClassWrapper.getMethod( theClass, "getProperty", PProcessOID.class, String.class );
		String msg = (String)MethodWrapper.invoke( method, null, processOID, PersistUserSpaceTest.PERSIST_TEST_RESULT );
		
		return msg;
	}
}
