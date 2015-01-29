package ch.shaktipat.saraswati.test.instrument;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class InvocationVariantsTestSubClass extends InvocationVariantsTestSuperClass implements InvocationVariantsTestInterface
{
	private static final long serialVersionUID = 1L;
	
	private String constructorTest;
	
	InvocationVariantsTestSubClass()
	{
		this.constructorTest = "InvocationVariantsTestSubClass()";
	}
	
	InvocationVariantsTestSubClass( String constructorTest )
	{
		// invokes super class's default constructor
		this.constructorTest = constructorTest;
	}
	
	InvocationVariantsTestSubClass( String constructorTest, String superConstructorTest )
	{
		super( superConstructorTest );
		this.constructorTest = constructorTest;
	}
	
	InvocationVariantsTestSubClass( boolean doesntMatter )
	{
		InvocationVariantsTestSubClass c = new InvocationVariantsTestSubClass(
			"InvocationVariantsTestSubClass( String constructorTest )" );
		this.constructorTest = c.constructorTest;
	}
	
	InvocationVariantsTestSubClass( int doesntMatter )
	{
		super( new InvocationVariantsTestSuperClass() );
		this.constructorTest = "testing2";
	}
	
	@Override
	public String overridableMethod()
	{
		return "InvocationVariantsTestSubClass.overridableMethod()";
	}
	
	@Override
	public String overridableMethodWithSuperClassInvocation()
	{
		return super.overridableMethodWithSuperClassInvocation();
	}

	public static String staticMethod()
	{
		return "InvocationVariantsTestSubClass.staticMethod()";
	}
	
	public String staticMethodInvoker()
	{
		return InvocationVariantsTestSubClass.staticMethod();
	}

	@Override
	public String interfaceMethod()
	{
		return "InvocationVariantsTestSubClass.interfaceMethod()";
	}
	
	public String interfaceMethodInvoker()
	{
		InvocationVariantsTestInterface ivtInterface = this;
		return ivtInterface.interfaceMethod();
	}
	
	public String uninstrumentedInstantiation()
	{
		String s = new String( "Uninstrumented Instantiation Parameter" );
		return s;
	}
	
	public String instrumentedInstantiation()
	{
		InvocationVariantsTestSubClass c = new InvocationVariantsTestSubClass();
		return c.constructorTest;
	}
	
	public String instrumentedInstantiationWithParam()
	{
		InvocationVariantsTestSubClass c = new InvocationVariantsTestSubClass( "InvocationVariantsTestSubClass( String constructorTest )" );
		return c.constructorTest;
	}
	
	public String instrumentedInstantiationWithSuperInvocation()
	{
		InvocationVariantsTestSubClass c = new InvocationVariantsTestSubClass(
			"InvocationVariantsTestSubClass( String constructorTest )",
			"InvocationVariantsTestSuperClass( String superConstructorTest )" );
		return c.constructorTest + " " + c.superConstructorTest;
	}
	
	public String instrumentedInstantiationInConstructor()
	{
		InvocationVariantsTestSubClass c = new InvocationVariantsTestSubClass( true );
		return c.constructorTest;
	}
}
