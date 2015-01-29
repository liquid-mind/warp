package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class InvocationVariantsTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public String superClassMethod()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.superClassMethod();
	}
	
	public String overridableMethod()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.overridableMethod();
	}
	
	public String overridableMethodWithSuperClassInvocation()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.overridableMethodWithSuperClassInvocation();
	}

	public String staticMethod()
	{
		return InvocationVariantsTestSubClass.staticMethod();
	}
	
	public String staticMethodInvoker()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.staticMethodInvoker();
	}

	public String interfaceMethod()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.interfaceMethod();
	}
	
	public String interfaceMethodInvoker()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.interfaceMethodInvoker();
	}
	
	public String uninstrumentedInstantiation()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.uninstrumentedInstantiation();
	}
	
	public String instrumentedInstantiation()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.instrumentedInstantiation();
	}
	
	public String instrumentedInstantiationWithParam()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.instrumentedInstantiationWithParam();
	}
	
	public String instrumentedInstantiationWithSuperInvocation()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.instrumentedInstantiationWithSuperInvocation();
	}
	
	public String instrumentedInstantiationInConstructor()
	{
		InvocationVariantsTestSubClass subClass = new InvocationVariantsTestSubClass();
		return subClass.instrumentedInstantiationInConstructor();
	}
}
