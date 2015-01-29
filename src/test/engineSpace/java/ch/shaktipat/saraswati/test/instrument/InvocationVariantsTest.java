package ch.shaktipat.saraswati.test.instrument;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.shaktipat.saraswati.test.common.AbstractPersistentInvocationBaseTest;

public class InvocationVariantsTest extends AbstractPersistentInvocationBaseTest
{
	public InvocationVariantsTest()
	{
		super( "ch.shaktipat.saraswati.test.instrument.InvocationVariantsTestClass" );
	}

	@Test
	public void testSuperClassMethod() throws Throwable
	{
		Object result = invokePersistentMethod( "superClassMethod", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSuperClass.superClassMethod()" ) );
	}
	
	@Test
	public void testOverridableMethod() throws Throwable
	{
		Object result = invokePersistentMethod( "overridableMethod", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass.overridableMethod()" ) );
	}
	
	@Test
	public void testOverridableMethodWithSuperClassInvocation() throws Throwable
	{
		Object result = invokePersistentMethod( "overridableMethodWithSuperClassInvocation", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSuperClass.overridableMethodWithSuperClassInvocation()" ) );
	}
	
	@Test
	public void testStaticMethodInvoker() throws Throwable
	{
		Object result = invokePersistentMethod( "staticMethodInvoker", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass.staticMethod()" ) );
	}
	
	@Test
	public void testInterfaceMethodInvoker() throws Throwable
	{
		Object result = invokePersistentMethod( "interfaceMethodInvoker", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass.interfaceMethod()" ) );
	}
	
	@Test
	public void testUninstrumentedInstantiation() throws Throwable
	{
		Object result = invokePersistentMethod( "uninstrumentedInstantiation", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "Uninstrumented Instantiation Parameter" ) );
	}
	
	@Test
	public void testInstrumentedInstantiation() throws Throwable
	{
		Object result = invokePersistentMethod( "instrumentedInstantiation", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass()" ) );
	}
	
	@Test
	public void testInstrumentedInstantiationWithParam() throws Throwable
	{
		Object result = invokePersistentMethod( "instrumentedInstantiationWithParam", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass( String constructorTest )" ) );
	}
	
	@Test
	public void testInstrumentedInstantiationWithSuperInvocation() throws Throwable
	{
		Object result = invokePersistentMethod( "instrumentedInstantiationWithSuperInvocation", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass( String constructorTest ) InvocationVariantsTestSuperClass( String superConstructorTest )" ) );
	}
	
	@Test
	public void testInstrumentedInstantiationInConstructor() throws Throwable
	{
		Object result = invokePersistentMethod( "instrumentedInstantiationInConstructor", new Class< ? >[] {}, new Object[] {} );
		assertTrue( result != null );
		assertTrue( result.getClass().equals( String.class ) );
		assertTrue( ((String)result).equals( "InvocationVariantsTestSubClass( String constructorTest )" ) );
	}
}
