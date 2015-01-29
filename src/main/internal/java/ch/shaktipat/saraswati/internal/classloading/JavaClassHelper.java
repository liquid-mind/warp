package ch.shaktipat.saraswati.internal.classloading;

import java.util.HashMap;
import java.util.Map;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper;

/*
 * Rather confusingly, there are a number of ways in which Java classes
 * can be represented in string and object form:
 * 
 * 1. As field descriptors conforming to JVM 7 spec, chapter 4.3.:
 *       Ljava/lang/String;
 *       [Ljava/lang/String;
 *       B
 *       [B
 * 
 * 2. Strings returned from Class.getName():
 *       java.lang.String
 *       [Ljava.lang.String;
 *       byte
 *       [B
 *       
 * 3. Java Class< T > objects.
 * 4. Javassist CtClass objects.
 * 5. Javassist Type objects.
 * 
 * Note that Type objects are essentially supersets of CtClass objects
 * that include special JVM internal information (not sure what). Type
 * objects are used by the javassist.bytecode.analysis package.
 * 
 * Adding to the confusion is the fact that even though Java primitives
 * have class objects (e.g., byte.class, int.class, etc.) it is not
 * possible to load these through a classloader.
 */
public class JavaClassHelper
{
	private static final Map< String, Class< ? > > primitivesMap = new HashMap< String, Class< ? > >();

	static
	{
		primitivesMap.put( byte.class.getName(), byte.class );
		primitivesMap.put( short.class.getName(), short.class );
		primitivesMap.put( int.class.getName(), int.class );
		primitivesMap.put( long.class.getName(), long.class );
		primitivesMap.put( char.class.getName(), char.class );
		primitivesMap.put( boolean.class.getName(), boolean.class );
		primitivesMap.put( double.class.getName(), double.class );
		primitivesMap.put( float.class.getName(), float.class );
		primitivesMap.put( void.class.getName(), void.class );
	}

	public static Class< ? > getClass( String className )
	{
		Class< ? > theClass = null;

		if ( primitivesMap.containsKey( className ) )
		{
			theClass = primitivesMap.get( className );
		}
		else
		{
			theClass = ClassWrapper.forName( className, false, Thread.currentThread().getContextClassLoader() );
		}

		return theClass;
	}

	public static Class< ? >[] getClasses( String[] classNames )
	{
		Class< ? >[] classes = new Class< ? >[ classNames.length ];

		for ( int i = 0; i < classNames.length; ++i )
			classes[ i ] = getClass( classNames[ i ] );

		return classes;
	}
	
	public static String[] getClassNames( Class< ? >[] classes )
	{
		String[] classNames = new String[ classes.length ];
		
		for ( int i = 0 ; i < classNames.length ; ++i )
			classNames[ i ] = classes[ i ].getName();
			
		return classNames;
	}
	
	public static Class< ? >[] getParamTypes( String methodDescriptor )
	{
		String[] paramTypeNames = JVMDescriptorHelper.getParamTypesAsJavaClassNames( methodDescriptor );
		Class< ? >[] paramTypes = getClasses( paramTypeNames );
		
		return paramTypes;
	}
	
	public static Class< ? > getReturnType( String methodDescriptor )
	{
		String returnTypeName = JVMDescriptorHelper.getReturnAsJavaClassName( methodDescriptor );
		Class< ? > returnType = getClass( returnTypeName );
		
		return returnType;
	}
	
	public static Class< ? > getClassWithJVMName( String jvmClassName )
	{
		String classname = JVMDescriptorHelper.getFieldDescriptorAsJavaClassName( jvmClassName );
		Class< ? > theClass = getClass( classname );
		
		return theClass;
	}
}
