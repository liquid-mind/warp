package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.common.ReflectionHelper;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.instrument.SymbolRegistry;
import ch.shaktipat.saraswati.internal.instrument.method.MethodInvocationType;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper;

// Used by PFrame to remember which method and parameters
// originally caused the frame to be pushed onto the stack.
// Use by PProcess to point to the next method to invoke
// (similar to a program counter in a VM).
public class MethodInvocationInstance implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private MethodInvocationType methodInvocationType;
	private FQJVMMethodName fqMethodName;
	private Object objectInstance;
	private Object[] parameters;
	private boolean requiresObjectConstruction;
	private FQJVMMethodName resolvedFqMethodName;
	
	public MethodInvocationInstance( MethodInvocationType methodInvocationType, FQJVMMethodName fqMethodName, Object objectInstance, Object[] parameters, boolean requiresObjectConstruction )
	{
		super();
		this.methodInvocationType = methodInvocationType;
		this.fqMethodName = fqMethodName;
		this.objectInstance = objectInstance;
		this.parameters = parameters;
		this.requiresObjectConstruction = requiresObjectConstruction;
		this.resolvedFqMethodName = resolvedFQMethodName();
	}

	public MethodInvocationType getMethodInvocationType()
	{
		return methodInvocationType;
	}

	public void setMethodInvocationType( MethodInvocationType methodInvocationType )
	{
		this.methodInvocationType = methodInvocationType;
	}

	public Object[] getParameters()
	{
		return parameters;
	}

	public void setParameters( Object[] parameters )
	{
		this.parameters = parameters;
	}

	public Object getObjectInstance()
	{
		return objectInstance;
	}

	public void setObjectInstance( Object objectInstance )
	{
		this.objectInstance = objectInstance;
	}

	public FQJVMMethodName getFQMethodName()
	{
		return fqMethodName;
	}

	public void setFQMethodName( FQJVMMethodName fqMethodName )
	{
		this.fqMethodName = fqMethodName;
	}

	public FQJVMMethodName getResolvedFqMethodName()
	{
		return resolvedFqMethodName;
	}

	public void setResolvedFqMethodName( FQJVMMethodName resolvedFqMethodName )
	{
		this.resolvedFqMethodName = resolvedFqMethodName;
	}
	
	public boolean isResolvedClassPersistent()
	{
		SymbolRegistry symbolRegistry = PEngine.getPEngine().getPClassLoader().getSymbolRegistry();
		boolean isPersistentClass = symbolRegistry.isPersistentClass( resolvedFqMethodName.getJVMClassName() );
		
		return isPersistentClass;
	}

	// Note that the process stores the MethodInvocationInstance as
	// "raw" data, meaning the data as it was when the method was instrumented.
	// So, e.g., the method name is the original name, not the Saraswati
	// name; the class name is the name of the type that the method
	// was invoked on rather than the actual target class (after
	// virtual method resolution).
	// This method returns the a value containing the resolved class
	// and method, ready for invocation through the engine.
	public FQJVMMethodName resolvedFQMethodName()
	{
		String actualClassName = getActualClassName();
		String resolvedClassName = getResolvedClassName( actualClassName );
		String resolvedMethodName = getResolvedMethodName( resolvedClassName );
		
		return new FQJVMMethodName( resolvedClassName, resolvedMethodName, fqMethodName.getJVMMethodDescriptor() );
	}

	public boolean getRequiresObjectConstruction()
	{
		return requiresObjectConstruction;
	}

	public void setRequiresObjectConstruction( boolean requiresObjectConstruction )
	{
		this.requiresObjectConstruction = requiresObjectConstruction;
	}
	
	private String getActualClassName()
	{
		String targetClassName = null;
		
		if ( ( methodInvocationType.equals( MethodInvocationType.INVOKE_VIRTUAL ) ||  methodInvocationType.equals( MethodInvocationType.INVOKE_INTERFACE ) ) && objectInstance != null )
			targetClassName = JVMDescriptorHelper.getJVMFieldDescriptor( objectInstance.getClass() );
		else
			targetClassName = fqMethodName.getJVMClassName();
		
		return targetClassName;
	}
	
	private String getResolvedClassName( String actualClassName )
	{
		SymbolRegistry symbolRegistry = PEngine.getPEngine().getPClassLoader().getSymbolRegistry();
		String resolvedClassName = null;

		if ( ( methodInvocationType.equals( MethodInvocationType.INVOKE_SPECIAL ) ||  methodInvocationType.equals( MethodInvocationType.INVOKE_STATIC ) ) )
			resolvedClassName = actualClassName;
		else if ( symbolRegistry.isPersistentClass( actualClassName ) )
			resolvedClassName = getResolvedClassNameOfPersistentClass( actualClassName );
		else
			resolvedClassName = getResolvedClassNameOfNonPersistentClass( actualClassName );
		
		return resolvedClassName;
	}
	
	private String getResolvedClassNameOfPersistentClass( String actualClassName )
	{
		SymbolRegistry symbolRegistry = PEngine.getPEngine().getPClassLoader().getSymbolRegistry();
		String resolvedClassName = null;
		FQJVMMethodName actualFQMethodName = new FQJVMMethodName( actualClassName, fqMethodName.getJVMMethodName(), fqMethodName.getJVMMethodDescriptor() );
		resolvedClassName = symbolRegistry.resolveVirtualInvocation( actualFQMethodName );
		
		return resolvedClassName;
	}
	
	private String getResolvedClassNameOfNonPersistentClass( String actualClassName )
	{
		Class< ? > actualClass = JavaClassHelper.getClassWithJVMName( actualClassName );
		String methodName = fqMethodName.getJVMMethodName();
		Class< ? >[] paramTypes = JavaClassHelper.getParamTypes( fqMethodName.getJVMMethodDescriptor() );
		Class< ? > resolvedClass = null;
		
		if ( methodName.equals( FieldAndMethodConstants.INIT_METHOD ) )
		{
			Constructor< ? > constructor = ReflectionHelper.getConstructor( actualClass, paramTypes );
			resolvedClass = constructor.getDeclaringClass();
		}
		else
		{
			Method method = ReflectionHelper.getMethod( actualClass, methodName, paramTypes );
			resolvedClass = method.getDeclaringClass();
		}
			
		String resolvedClassName = JVMDescriptorHelper.getJVMFieldDescriptor( resolvedClass );
		
		return resolvedClassName;
	}
	
	private String getResolvedMethodName( String resolvedClassName )
	{
		SymbolRegistry symbolRegistry = PEngine.getPEngine().getPClassLoader().getSymbolRegistry();
		FQJVMMethodName targetFQMethodName = new FQJVMMethodName( resolvedClassName, fqMethodName.getJVMMethodName(), fqMethodName.getJVMMethodDescriptor() );
		String targetMethodName = null;

		if ( symbolRegistry.isPersistentClass( resolvedClassName ) )
			targetMethodName = symbolRegistry.getSaraswatiMethodName( targetFQMethodName );
		else
			targetMethodName = fqMethodName.getJVMMethodName();
		
		return targetMethodName;
	}
	
	private void writeObject( ObjectOutputStream out ) throws IOException
	{
		out.writeObject( methodInvocationType );
		out.writeObject( fqMethodName );
		out.writeObject( objectInstance );
		out.writeObject( parameters );
		out.writeBoolean( requiresObjectConstruction );
	}
	
	private void readObject( ObjectInputStream in ) throws IOException, ClassNotFoundException
	{
		methodInvocationType = (MethodInvocationType)in.readObject();
		fqMethodName = (FQJVMMethodName)in.readObject();
		objectInstance = in.readObject();
		parameters = (Object[])in.readObject();
		requiresObjectConstruction = in.readBoolean();
		resolvedFqMethodName = resolvedFQMethodName();
	}
	
	@Override
	public String toString()
	{
		String stringValue = this.getClass().getName() + "@" + this.hashCode() + System.lineSeparator();
		
		stringValue += "{" + System.lineSeparator();
		
		stringValue += "\tmethodInvocationType = " + methodInvocationType + System.lineSeparator();
		stringValue += "\tfqMethodName.jvmClassName = " + fqMethodName.getJVMClassName() + System.lineSeparator();
		stringValue += "\tfqMethodName.jvmMethodName = " + fqMethodName.getJVMMethodName() + System.lineSeparator();
		stringValue += "\tfqMethodName.jvmMethodDescriptor = " + fqMethodName.getJVMMethodDescriptor() + System.lineSeparator();
		stringValue += "\trequiresObjectConstruction = " + requiresObjectConstruction + System.lineSeparator();
		stringValue += "\tobjectInstance = " + objectInstance + System.lineSeparator();
		
		for ( Object parameter : parameters )
			stringValue += "\tparameter = " + parameter + System.lineSeparator();
		
		stringValue += "}" + System.lineSeparator();
		
		return stringValue;
	}
}
