package ch.shaktipat.saraswati.test.common;

import java.lang.reflect.Method;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.MethodWrapper;
import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.internal.engine.PEngine;

public abstract class AbstractUserSpaceMethodBaseTest
{
	private static final String ENGINE_SPACE = "EngineSpace";
	private static final String USER_SPACE = "UserSpace";
	
	private Class< ? > userSpaceTestClass;
	private Object userSpaceTestObject;

	protected AbstractUserSpaceMethodBaseTest()
	{
		super();
		String userSpaceTestClassName = getDefaultUserSpaceTestClassName();
		setup( userSpaceTestClassName );
	}
	
	private String getDefaultUserSpaceTestClassName()
	{
		String className = this.getClass().getName();
		String defaultClassName = className.replace( ENGINE_SPACE, USER_SPACE );
		
		return defaultClassName;
	}

	protected AbstractUserSpaceMethodBaseTest( String userSpaceTestClassName )
	{
		super();
		setup( userSpaceTestClassName );
	}
	
	private void setup( String userSpaceTestClassName )
	{
		// Make sure the context class loader was set up.
		PEngine.getPEngine();
		
		userSpaceTestClass = JavaClassHelper.getClass( userSpaceTestClassName );
		userSpaceTestObject = ClassWrapper.newInstance( userSpaceTestClass );
	}

	protected void invokeTestMethod()
	{
		String methodName = Thread.currentThread().getStackTrace()[ 2 ].getMethodName();
		invokeTestMethod( methodName );
	}
	
	protected void invokeTestMethod( String methodName )
	{
		Method method = ClassWrapper.getMethod( userSpaceTestClass, methodName, new Class< ? >[] {} );
		MethodWrapper.invoke( method, userSpaceTestObject );
	}
}
