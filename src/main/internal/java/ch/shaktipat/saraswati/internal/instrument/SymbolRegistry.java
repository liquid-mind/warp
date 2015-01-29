package ch.shaktipat.saraswati.internal.instrument;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ch.shaktipat.exwrapper.javassist.CtBehaviorWrapper;
import ch.shaktipat.exwrapper.javassist.CtClassWrapper;
import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.common.SaraswatiHashMap;
import ch.shaktipat.saraswati.internal.common.SaraswatiHashSet;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.volatility.PersistentMethod;
import ch.shaktipat.saraswati.volatility.Volatility;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Descriptor;

public class SymbolRegistry
{
	private Set< String > persistentClasses = new SaraswatiHashSet< String >();
	private Map< String, String > classToSourceFileNameMap = new SaraswatiHashMap< String, String >();
	private Map< FQJVMMethodName, Volatility > methodVolatilityMap = new SaraswatiHashMap< FQJVMMethodName, Volatility >();
	private Map< FQJVMMethodName, String > virtualInvocationMap = new SaraswatiHashMap< FQJVMMethodName, String >();
	private Map< FQJVMMethodName, String > saraswatiMethodNameMap = new SaraswatiHashMap< FQJVMMethodName, String >();

	public void registerClass( CtClass ctClass )
	{
		registerPersistentClass( ctClass );
		registerSourceFileName( ctClass );
		registerMethodVolatilities( ctClass );
		registerVirtualInvocations( ctClass );
		registerSaraswatiMethodNames( ctClass );
	}
	
	private void registerPersistentClass( CtClass ctClass )
	{
		if ( isPersistentClass( ctClass ) )
			persistentClasses.add( Descriptor.of( ctClass ) );
	}
	
	private void registerSourceFileName( CtClass ctClass )
	{
		classToSourceFileNameMap.put( Descriptor.of( ctClass ), ctClass.getClassFile().getSourceFile() );
	}
	
	public static boolean isPersistentClass( CtClass ctClass )
	{
		return ctClass.hasAnnotation( PersistentClass.class );
	}
	
	public boolean isPersistentClass( String jvmClassName )
	{
		return persistentClasses.contains( jvmClassName );
	}
	
	private void registerMethodVolatilities( CtClass ctClass )
	{
		// Skip non-persistent classes.
		if ( !isPersistentClass( ctClass ) )
			return;
		
		CtBehavior[] ctBehaviors = ctClass.getDeclaredBehaviors();
		
		for ( CtBehavior ctBehavior : ctBehaviors)
		{
			Volatility volatility = null;
			
			// Is this the static initializer? If so --> force VOLATILE.
			if ( ctBehavior.getName().equals( FieldAndMethodConstants.CLINIT_METHOD ) )
			{
				volatility = Volatility.VOLATILE;
			}
			// Otherwise, get volatility from method annotation.
			else
			{
				PersistentMethod persistentMethod = (PersistentMethod)CtBehaviorWrapper.getAnnotation( ctBehavior, PersistentMethod.class );

				if ( persistentMethod != null )
					volatility = persistentMethod.volatility();
			}
			
			// If no volatility was defined --> set the default.
			if ( volatility == null )
				volatility = Volatility.getDefault();
			
			methodVolatilityMap.put( new FQJVMMethodName( ctClass, ctBehavior ), volatility );
		}
	}
	
	private void registerVirtualInvocations( CtClass ctClass )
	{
		// Ignore classes that are not persistent and have no persistent ancestors.
		if ( !isClassOrAncestorPersistent( ctClass ) )
			return;
		
		registerVirtualInvocations( ctClass, ctClass, null );
	}

	private void registerVirtualInvocations( CtClass targetedClass, CtClass currentClass, Set< FQJVMMethodName > handledMethods )
	{
		CtMethod[] ctMethods = getNonSaraswatiMethods( currentClass );

		// Initialize on first invocation
		if ( handledMethods == null )
			handledMethods = new HashSet< FQJVMMethodName >();

		for ( CtMethod ctMethod : ctMethods )
		{
			// Skip non-virtual methods (i.e. private or static methods)
			if ( ( ( ctMethod.getModifiers() & Modifier.PRIVATE ) != 0 ) || ( ( ctMethod.getModifiers() & Modifier.STATIC ) != 0 ) )
				continue;

			FQJVMMethodName targetedMethod = new FQJVMMethodName( targetedClass, ctMethod );

			// Skip any methods that were already handled in a subclass
			if ( handledMethods.contains( targetedMethod ) )
				continue;
			
			virtualInvocationMap.put( targetedMethod, Descriptor.of( currentClass ) );
			handledMethods.add( targetedMethod );
		}

		// If the super class is java.lang.Object --> we are done
		// (java.lang.Object is *not* instrumented)
		CtClass superClass = CtClassWrapper.getSuperclass( currentClass );
		if ( superClass == null )
			return;

		// Otherwise, register recursively
		registerVirtualInvocations( targetedClass, superClass, handledMethods );
	}

	private CtMethod[] getNonSaraswatiMethods( CtClass ctClass )
	{
		CtMethod[] allMethods = ctClass.getDeclaredMethods();
		Set< CtMethod > nonSaraswatiMethods = new HashSet< CtMethod >();
		
		for ( CtMethod method : allMethods )
			if ( !method.getName().startsWith( FieldAndMethodConstants.SARASWATI_PREFIX ) )
				nonSaraswatiMethods.add( method );
		
		return nonSaraswatiMethods.toArray( new CtMethod[ nonSaraswatiMethods.size() ] );
	}
	
	private void registerSaraswatiMethodNames( CtClass ctClass )
	{
		// Skip non-persistent classes.
		if ( !isPersistentClass( ctClass ) )
			return;
		
		String saraswatiMethodName = null;
		CtBehavior[] ctBehaviors = ctClass.getDeclaredBehaviors();
		
		for ( CtBehavior ctBehavior : ctBehaviors)
		{
			// Note that MethodInfo.getName() returns "<init>" for constructors,
			// whereas Behavior.getName() returns the class name.
			String behaviorName = ctBehavior.getMethodInfo().getName();
			
			// Is this the static initializer? If so --> get volatility from class annotation.
			if ( behaviorName.equals( FieldAndMethodConstants.CLINIT_METHOD ) )
				saraswatiMethodName = FieldAndMethodConstants.SARASWATI_CLINIT_METHOD;
			else if ( behaviorName.equals( FieldAndMethodConstants.INIT_METHOD ) )
				saraswatiMethodName = FieldAndMethodConstants.SARASWATI_INIT_METHOD;
			else
				saraswatiMethodName = FieldAndMethodConstants.SARASWATI_PREFIX + behaviorName;
			
			saraswatiMethodName = saraswatiMethodName + "_" + UUID.randomUUID();
			FQJVMMethodName fqMethodName = new FQJVMMethodName( ctClass, ctBehavior );
			saraswatiMethodNameMap.put( fqMethodName, saraswatiMethodName );
		}
	}
	
	private boolean isClassOrAncestorPersistent( CtClass ctClass )
	{
		boolean isClassOrAncestorPersistent = false;
		
		CtClass ctClassPointer = ctClass;
		
		while ( ctClassPointer != null )
		{
			if ( isPersistentClass( ctClassPointer ) )
			{
				isClassOrAncestorPersistent = true;
				break;
			}
			
			ctClassPointer = CtClassWrapper.getSuperclass( ctClassPointer );
		}
		
		return isClassOrAncestorPersistent;
	}

	public String getSaraswatiMethodName( CtClass ctClass, CtBehavior ctBehavior )
	{
		return getSaraswatiMethodName( new FQJVMMethodName( ctClass, ctBehavior ) );
	}
	
	public String getSaraswatiMethodName( FQJVMMethodName fqMethodName )
	{
		return saraswatiMethodNameMap.get( fqMethodName );
	}

	public String getSourceFileName( String jvmClassName )
	{
		return classToSourceFileNameMap.get( jvmClassName );
	}

	public Volatility getMethodVolatility( FQJVMMethodName methodName )
	{
		return methodVolatilityMap.get( methodName );
	}
	
	public String resolveVirtualInvocation( FQJVMMethodName methodName )
	{
		return virtualInvocationMap.get( methodName );
	}
	
	public void dumpVirtualInvocationMapForDebugging()
	{
		System.out.println( virtualInvocationMap );
	}
}
