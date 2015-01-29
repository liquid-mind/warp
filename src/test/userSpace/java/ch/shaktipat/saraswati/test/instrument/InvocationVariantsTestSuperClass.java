package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class InvocationVariantsTestSuperClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected String superConstructorTest;
	
	public InvocationVariantsTestSuperClass()
	{}
	
	public InvocationVariantsTestSuperClass( String superConstructorTest )
	{
		this.superConstructorTest = superConstructorTest;
	}
	
	public InvocationVariantsTestSuperClass( InvocationVariantsTestSuperClass c )
	{
	}
	
	public String superClassMethod()
	{
		return "InvocationVariantsTestSuperClass.superClassMethod()";
	}
	
	public String overridableMethod()
	{
		return "InvocationVariantsTestSuperClass.overridableMethod()";
	}
	
	public String overridableMethodWithSuperClassInvocation()
	{
		return "InvocationVariantsTestSuperClass.overridableMethodWithSuperClassInvocation()";
	}
}
