package ch.shaktipat.saraswati.internal.classloading;

import java.lang.reflect.Method;
import java.security.SecureClassLoader;

import javassist.CtBehavior;
import javassist.CtClass;
import ch.shaktipat.exwrapper.javassist.ClassPoolWrapper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.instrument.ClassInstrumenter;
import ch.shaktipat.saraswati.internal.instrument.SymbolRegistry;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegmentResult;

public class PClassLoader extends SecureClassLoader
{
	private PClassPool pClassPool;
	private SymbolRegistry symbolRegistry;
	
	public PClassLoader()
	{
		super( ClassLoader.getSystemClassLoader() );

		pClassPool = new PClassPool();
		symbolRegistry = new SymbolRegistry();
	}
	
	@Override
	protected Class< ? > findClass( String className ) throws ClassNotFoundException
	{
		Class< ? > theClass = findClassFromSystemLoader( className );
		
		if ( theClass == null )
			theClass = findClassFromSaraswatiClasspath( className );
		
		return theClass;
	}
	
	private Class< ? > findClassFromSystemLoader( String className )
	{
		Class< ? > theClass = null;
		
		try
		{
			theClass = super.findClass( className );
		}
		catch ( ClassNotFoundException e )
		{}
		
		return theClass;
	}
	
	private Class< ? > findClassFromSaraswatiClasspath( String className ) throws ClassNotFoundException
	{
		Class< ? > theClass = null;
		
		try
		{
			if ( pClassPool.isClassInSaraswatiClassPath( className ) )
			{
				CtClass ctClass = ClassPoolWrapper.get( pClassPool, className );
				
				// This is the main "workflow" of class loading.
				verifyClass( ctClass );
				registerClass( ctClass );
				theClass = instrumentClass( ctClass );
				initalizeClass( ctClass, theClass );
			}
		}
		catch ( Throwable t )
		{
			throw new ClassNotFoundException( "Error loading class: " + className, t );
		}
		
		return theClass;
	}
	
	private void verifyClass( CtClass ctClass ) throws VerifyError
	{}

	private void registerClass( CtClass ctClass )
	{
		symbolRegistry.registerClass( ctClass );
	}

	private Class< ? > instrumentClass( CtClass ctClass )
	{
		Class< ? > theClass = null;
		byte[] instrumentedByteCode = null;
		ClassInstrumenter classInstrumenter = new ClassInstrumenter( ctClass );
		
		if ( SymbolRegistry.isPersistentClass( ctClass ) )
			instrumentedByteCode = classInstrumenter.getByteCodeForPersistentClass();
		else
			instrumentedByteCode =  classInstrumenter.getByteCodeForNonPersistentClass();
		
		theClass = defineClass( ctClass.getName(), instrumentedByteCode, 0, instrumentedByteCode.length );
		
		return theClass;
	}
	
	private void initalizeClass( CtClass ctClass, Class< ? > theClass ) throws Throwable
	{
		// If this is not a persistent class --> return.
		// (In this case the static initializer is invoked
		// by the jvm classloader)
		if ( !SymbolRegistry.isPersistentClass( ctClass ) )
			return;

		// Force the JVM to invoke the static initializer by access a member. Without this step
		// the JVM is free to defer invoking the static initializer until such an access takes place.
		// If the class is loaded outside of a persistent process context but then later accessed
		// for the first time within such a context, the static intializer never gets invoked.
		if ( !( ctClass.isInterface() || ctClass.isEnum() || ctClass.isAnnotation() ) )
		{
			Method method = theClass.getMethod( FieldAndMethodConstants.SARASWATI_FORCE_STATIC_INITIALIZATION_METHOD, new Class< ? >[] {} );
			method.invoke( null, new Object[] {} );
		}
		
		PersistentProcess pProcess = PersistentProcessImpl.getSelfProxyCurrentProcess();
		
		// If there is no current persistent process -->
		// do not execute static initializer through the engine
		// (in this case, it will be done in the normal way through the JVM).
		if ( pProcess == null )
			return;
		
		// If there is no static initializer --> return.
		if ( !hasStaticInitializer( ctClass ) )
			return;
		
		// We are essentially interrupting the current process executor to
		// run this static initializer --> create a new one and run the
		// initializer in that.
		PProcessExecutionSegmentResult result = pProcess.invokeStaticIntializer( theClass );
		
		// Static initializers have no args and no return value, but they
		// can throw execeptoins. If this happens, we need to ensure that
		// a ClassNotFoundException is generated and thrown (see above).
		// This will be caught in PProcessExecuter and handled in the
		// normal way, i.e., passing it through the call-stack for
		// handling.
		if ( result.getException() != null )
			throw result.getException();
	}
	
	private boolean hasStaticInitializer( CtClass ctClass )
	{
		boolean hasStaticInitializer = false;
		
		for ( CtBehavior ctBehavior : ctClass.getDeclaredBehaviors() )
		{
			if ( ctBehavior.getName().equals( FieldAndMethodConstants.CLINIT_METHOD ) )
			{
				hasStaticInitializer = true;
				break;
			}
		}
		
		return hasStaticInitializer;
	}

	public SymbolRegistry getSymbolRegistry()
	{
		return symbolRegistry;
	}

	public PClassPool getPClassPool()
	{
		return pClassPool;
	}
}
