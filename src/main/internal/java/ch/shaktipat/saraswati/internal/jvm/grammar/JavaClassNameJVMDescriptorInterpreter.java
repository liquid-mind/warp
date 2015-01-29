package ch.shaktipat.saraswati.internal.jvm.grammar;

import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClassNameJVMDescriptorInterpreter extends AbstractJVMDescriptorInterpreter
{
	private static final Map< String, String > primitivesMap = new HashMap< String, String >();

	static
	{
		primitivesMap.put( BYTE_JVM_TYPE_NAME, byte.class.getName() );
		primitivesMap.put( CHAR_JVM_TYPE_NAME, char.class.getName() );
		primitivesMap.put( SHORT_JVM_TYPE_NAME, short.class.getName() );
		primitivesMap.put( INT_JVM_TYPE_NAME, int.class.getName() );
		primitivesMap.put( LONG_JVM_TYPE_NAME, long.class.getName() );
		primitivesMap.put( FLOAT_JVM_TYPE_NAME, float.class.getName() );
		primitivesMap.put( DOUBLE_JVM_TYPE_NAME, double.class.getName() );
		primitivesMap.put( BOOLEAN_JVM_TYPE_NAME, boolean.class.getName() );
		primitivesMap.put( VOID_JVM_TYPE_NAME, void.class.getName() );
	}
	
	private List< String > paramClassNames = new ArrayList< String >();
	private String returnClassName;
	private String className;
	
	@Override
	public void interpreteJVMParameterDescriptor( String jvmParameterDescriptor )
	{
		paramClassNames.add( convertToJavaClassName( jvmParameterDescriptor ) );
	}
	
	@Override
	public void interpreteJVMReturnDescriptor( String jvmReturnDescriptor )
	{
		returnClassName = convertToJavaClassName( jvmReturnDescriptor );
	}

	@Override
	public void interpreteJVMFieldDescriptor( String jvmFieldDescriptor )
	{
		className = convertToJavaClassName( jvmFieldDescriptor );
	}
	
	private String convertToJavaClassName( String jvmFieldType )
	{
		String className = null;
		
		if ( primitivesMap.keySet().contains( jvmFieldType ) )
		{
			className = primitivesMap.get( jvmFieldType );
		}
		else
		{
			className = jvmFieldType.replaceAll( "/", "." );
			
			if ( !className.startsWith( ARRAY_DIM_SYMBOL ) )
				className = className.substring( REFERENCE_JVM_TYPE_NAME.length(), className.length() - EO_REF_SYMBOL.length() );
		}
		
		return className;
	}

	public List< String > getParamClassNames()
	{
		return paramClassNames;
	}

	public String getReturnClassName()
	{
		return returnClassName;
	}

	public String getClassName()
	{
		return className;
	}
	
	public void reset()
	{
		paramClassNames.clear();
		returnClassName = null;
		className = null;
	}
}
