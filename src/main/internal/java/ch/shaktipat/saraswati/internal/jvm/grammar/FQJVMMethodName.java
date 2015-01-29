package ch.shaktipat.saraswati.internal.jvm.grammar;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.bytecode.Descriptor;

// Used by BytecodeGenerator to communicate to the engine
// which method of which class has been invoked.
// Also used by SymbolRegistry to map Java to Saraswati names.
public class FQJVMMethodName implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String jvmClassName;
	private String jvmMethodName;
	private String jvmMethodDescriptor;

	public FQJVMMethodName()
	{
		super();
	}

	public FQJVMMethodName( String jvmClassName, String jvmMethodName, String jvmMethodDescriptor )
	{
		super();
		this.jvmClassName = jvmClassName;
		this.jvmMethodName = jvmMethodName;
		this.jvmMethodDescriptor = jvmMethodDescriptor;
	}

	public FQJVMMethodName( FQJVMMethodName fqJVMMethodName )
	{
		this( fqJVMMethodName.jvmClassName, fqJVMMethodName.jvmMethodName, fqJVMMethodName.jvmMethodDescriptor );
	}

	public FQJVMMethodName( CtClass ctClass, CtBehavior ctBehavior )
	{
		// Note that MethodInfo.getName() returns "<init>" for constructors,
		// whereas Behavior.getName() returns the class name.
		this( Descriptor.of( ctClass ), ctBehavior.getMethodInfo().getName(), ctBehavior.getSignature() );
	}

	public FQJVMMethodName( Class< ? > theClass, Method method )
	{
		this(
			JVMDescriptorHelper.getJVMFieldDescriptor( theClass ),
			method.getName(),
			JVMDescriptorHelper.getJVMMethodDescriptor( method.getReturnType(), method.getParameterTypes() ) );
	}

	public FQJVMMethodName( Class< ? > theClass, String methodName, Class< ? > returnType, Class< ? > ... paramTypes )
	{
		this(
			JVMDescriptorHelper.getJVMFieldDescriptor( theClass ),
			methodName,
			JVMDescriptorHelper.getJVMMethodDescriptor( returnType, paramTypes ) );
	}

	public FQJVMMethodName( Class< ? > theClass, String methodName, Class< ? > returnType )
	{
		this( theClass, methodName, returnType, new Class[] {} );
	}
	
	public String getJVMClassName()
	{
		return jvmClassName;
	}

	public void setJVMClassName( String jvmClassName )
	{
		this.jvmClassName = jvmClassName;
	}

	public String getJVMMethodName()
	{
		return jvmMethodName;
	}

	public void setJVMMethodName( String jvmMethodName )
	{
		this.jvmMethodName = jvmMethodName;
	}

	public String getJVMMethodDescriptor()
	{
		return jvmMethodDescriptor;
	}

	public void setJVMMethodDescriptor( String jvmMethodDescriptor )
	{
		this.jvmMethodDescriptor = jvmMethodDescriptor;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( jvmClassName == null ) ? 0 : jvmClassName.hashCode() );
		result = prime * result + ( ( jvmMethodDescriptor == null ) ? 0 : jvmMethodDescriptor.hashCode() );
		result = prime * result + ( ( jvmMethodName == null ) ? 0 : jvmMethodName.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		FQJVMMethodName other = (FQJVMMethodName)obj;
		if ( jvmClassName == null )
		{
			if ( other.jvmClassName != null )
				return false;
		}
		else if ( !jvmClassName.equals( other.jvmClassName ) )
			return false;
		if ( jvmMethodDescriptor == null )
		{
			if ( other.jvmMethodDescriptor != null )
				return false;
		}
		else if ( !jvmMethodDescriptor.equals( other.jvmMethodDescriptor ) )
			return false;
		if ( jvmMethodName == null )
		{
			if ( other.jvmMethodName != null )
				return false;
		}
		else if ( !jvmMethodName.equals( other.jvmMethodName ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "FQJVMMethodName [jvmClassName=" + jvmClassName + ", jvmMethodName=" + jvmMethodName + ", jvmMethodDescriptor=" + jvmMethodDescriptor + "]";
	}
	
}
