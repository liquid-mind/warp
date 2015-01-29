package ch.shaktipat.saraswati.internal.jvm.grammar;

import java.util.List;

import javassist.CtClass;
import javassist.bytecode.analysis.Type;
import ch.shaktipat.exwrapper.javassist.bytecode.DescriptorWrapper;
import ch.shaktipat.saraswati.internal.classloading.PClassPool;
import ch.shaktipat.saraswati.internal.engine.PEngine;

// See "The Java Virtual Machine Specification" Java SE 7 Edition,
// Chapter 4.3., "Descriptors and Signatures" for the grammar that
// this class is based on.
// Note that javassist.bytecode.Descriptor has a similar function
// but the toJvmName() didn't seem to work with arrays. Also, I
// didn't really recognize the terms from the JVM grammar in the
// methods of that class, so rather than loosing more time I just
// made my own.
public class JVMDescriptorHelper
{
	// Symbols
	public static final String PARAM_BEGIN_SYMBOL = "(";
	public static final String PARAM_END_SYMBOL = ")";
	public static final String ARRAY_DIM_SYMBOL = "[";
	public static final String DOT_SYMBOL = ".";
	public static final String SLASH_SYMBOL = "/";

	// JVM Base Types
	public static final String BYTE_JVM_TYPE_NAME = "B";
	public static final String CHAR_JVM_TYPE_NAME = "C";
	public static final String SHORT_JVM_TYPE_NAME = "S";
	public static final String INT_JVM_TYPE_NAME = "I";
	public static final String LONG_JVM_TYPE_NAME = "J";
	public static final String FLOAT_JVM_TYPE_NAME = "F";
	public static final String DOUBLE_JVM_TYPE_NAME = "D";
	public static final String BOOLEAN_JVM_TYPE_NAME = "Z";

	// Void
	public static final String VOID_JVM_TYPE_NAME = "V";
	
	// Object type markers
	public static final String REFERENCE_JVM_TYPE_NAME = "L";
	public static final String EO_REF_SYMBOL = ";";
	
	private static JavaClassNameJVMDescriptorInterpreter javaClassNameInterpreter = new JavaClassNameJVMDescriptorInterpreter();
	private static JVMDescriptorParser javaClassNameParser = new JVMDescriptorParser( javaClassNameInterpreter );
	
	public static String getJVMFieldDescriptor( Class< ? > theClass )
	{
		return getJVMFieldType( theClass );
	}
	
	private static String getJVMFieldType( Class< ? > theClass )
	{
		String fieldType = null;
		
		if ( theClass.isArray() )
		{
			fieldType = getJVMArrayType( theClass );
		}
		else if ( theClass.isPrimitive() )
		{
			fieldType = getJVMBaseType( theClass );
		}
		else
		{
			// Must be an ObjectType.
			fieldType = getJVMObjectType( theClass );
		}
		
		return fieldType;
	}
	
	private static String getJVMArrayType( Class< ? > theClass )
	{
		assert( theClass.isArray() );
		
		String arrayType = ARRAY_DIM_SYMBOL + getJVMComponentType( theClass.getComponentType() );
		
		return arrayType;
	}
	
	private static String getJVMComponentType( Class< ? > theClass )
	{
		return getJVMFieldType( theClass );
	}
	
	private static String getJVMObjectType( Class< ? > theClass )
	{
		assert( !theClass.isArray() );
		assert( !theClass.isPrimitive() );
		
		String jvmObjectType = REFERENCE_JVM_TYPE_NAME + getJVMClassName( theClass ) + EO_REF_SYMBOL;

		return jvmObjectType;
	}
	
	public static String getJVMClassName( Class< ? > theClass )
	{
		assert( !theClass.isArray() );
		assert( !theClass.isPrimitive() );
		
		String className = theClass.getName().replace( DOT_SYMBOL, SLASH_SYMBOL );
		
		return className;
	}
		
	private static String getJVMBaseType( Class< ? > theClass )
	{
		assert( !theClass.isArray() );
		assert( theClass.isPrimitive() );
		
		String jvmBaseType = null;
		
		if ( theClass.equals( byte.class ) )
			jvmBaseType = BYTE_JVM_TYPE_NAME;
		else if ( theClass.equals( char.class ) )
			jvmBaseType = CHAR_JVM_TYPE_NAME;
		else if ( theClass.equals( short.class ) )
			jvmBaseType = SHORT_JVM_TYPE_NAME;
		else if ( theClass.equals( int.class ) )
			jvmBaseType = INT_JVM_TYPE_NAME;
		else if ( theClass.equals( long.class ) )
			jvmBaseType = LONG_JVM_TYPE_NAME;
		else if ( theClass.equals( float.class ) )
			jvmBaseType = FLOAT_JVM_TYPE_NAME;
		else if ( theClass.equals( double.class ) )
			jvmBaseType = DOUBLE_JVM_TYPE_NAME;
		else if ( theClass.equals( boolean.class ) )
			jvmBaseType = BOOLEAN_JVM_TYPE_NAME;
		else
			throw new IllegalStateException( "Unexpeced type: " + theClass );
		
		return jvmBaseType;
	}
	
	public static String getJVMMethodDescriptor( Class< ? > returnClass, Class< ? > ... paramClasses )
	{
		String parmDescriptors = "";
		
		for ( Class< ? > paramClass : paramClasses )
			parmDescriptors += getJVMParameterDescriptor( paramClass );
		
		String returnDescriptor = getJVMReturnDescriptor( returnClass );
		String methodDescriptor = PARAM_BEGIN_SYMBOL + parmDescriptors + PARAM_END_SYMBOL + returnDescriptor;
		
		return methodDescriptor;
	}
	
	private static String getJVMParameterDescriptor( Class< ? > paramClass )
	{
		return getJVMFieldType( paramClass );
	}
	
	private static String getJVMReturnDescriptor( Class< ? > returnClass )
	{
		String returnDescriptor = null;
		
		if ( returnClass.equals( void.class ) )
			returnDescriptor = getJVMVoidDescriptor();
		else
			returnDescriptor = getJVMFieldType( returnClass );
		
		return returnDescriptor;
	}
	
	private static String getJVMVoidDescriptor()
	{
		return VOID_JVM_TYPE_NAME;
	}
	
	public static CtClass[] getParamTypesAsCtClasses( String methodDescriptor )
	{
		PClassPool classPool = PEngine.getPEngine().getPClassLoader().getPClassPool();
		CtClass[] paramTypes = DescriptorWrapper.getParameterTypes( methodDescriptor, classPool );
		
		return paramTypes;
	}
	
	public static CtClass getReturnAsCtClass( String methodDescriptor )
	{
		PClassPool classPool = PEngine.getPEngine().getPClassLoader().getPClassPool();
		CtClass returnType = DescriptorWrapper.getReturnType( methodDescriptor, classPool );
		
		return returnType;
	}
	
	public static Type[] getParamTypesAsAnalysisTypes( String methodDescriptor )
	{
		CtClass[] paramTypesAsCtClasses = getParamTypesAsCtClasses( methodDescriptor );
		Type[] paramTypesAsAnalysisTypes = new Type[ paramTypesAsCtClasses.length ];
		
		for ( int i = 0 ; i < paramTypesAsAnalysisTypes.length ; ++i )
			paramTypesAsAnalysisTypes[ i ] = Type.get( paramTypesAsCtClasses[ i ] );
		
		return paramTypesAsAnalysisTypes;
	}
	
	public static Type getReturnAsAnalysisType( String methodDescriptor )
	{
		CtClass returnTypeAsCtClass = getReturnAsCtClass( methodDescriptor );
		Type returnTypeAsAnalysisType = Type.get( returnTypeAsCtClass );
		
		return returnTypeAsAnalysisType;
	}
	
	public static String[] getParamTypesAsJavaClassNames( String methodDescriptor )
	{
		javaClassNameInterpreter.reset();
		javaClassNameParser.parseJVMMethodDescriptor( methodDescriptor );
		List< String > paramClassNames = javaClassNameInterpreter.getParamClassNames();
		
		return paramClassNames.toArray( new String[ paramClassNames.size() ] );
	}
	
	public static String getReturnAsJavaClassName( String methodDescriptor )
	{
		javaClassNameInterpreter.reset();
		javaClassNameParser.parseJVMMethodDescriptor( methodDescriptor );
		
		return javaClassNameInterpreter.getReturnClassName();
	}
	
	public static String getFieldDescriptorAsJavaClassName( String fieldDescriptor )
	{
		javaClassNameInterpreter.reset();
		javaClassNameParser.parseJVMFieldDescriptor( fieldDescriptor );
		
		return javaClassNameInterpreter.getClassName();
	}
	
}
