package ch.shaktipat.saraswati.internal.jvm.grammar;

import static ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper.*;

public class JVMDescriptorParser
{
	private JVMDescriptorInterpreter interpreter;
	
	public JVMDescriptorParser( JVMDescriptorInterpreter interpreter )
	{
		super();
		this.interpreter = interpreter;
	}
	
	public String parseJVMMethodDescriptor( String jvmMethodDescriptorUnparsed )
	{
		String jvmMethodDescriptorParsed = null;
		String jvmMDUnparsedAsString = jvmMethodDescriptorUnparsed;

		if ( jvmMDUnparsedAsString.startsWith( PARAM_BEGIN_SYMBOL ) )
		{
			jvmMDUnparsedAsString = jvmMDUnparsedAsString.substring( PARAM_BEGIN_SYMBOL.length(), jvmMDUnparsedAsString.length() );
			jvmMethodDescriptorParsed = PARAM_BEGIN_SYMBOL;
			
			String jvmParamDescriptor = null;
			
			while ( (jvmParamDescriptor = parseJVMParameterDescriptor( jvmMDUnparsedAsString )) != null )
			{
				jvmMDUnparsedAsString = jvmMDUnparsedAsString.substring( jvmParamDescriptor.length(), jvmMDUnparsedAsString.length() );
				jvmMethodDescriptorParsed += jvmParamDescriptor;
			}

			if ( !jvmMDUnparsedAsString.startsWith( PARAM_END_SYMBOL ) )
				throw new IllegalStateException( "Ilformed method descriptor, missing: " + PARAM_END_SYMBOL );
			
			jvmMDUnparsedAsString = jvmMDUnparsedAsString.substring( PARAM_END_SYMBOL.length(), jvmMDUnparsedAsString.length() );
			jvmMethodDescriptorParsed += PARAM_END_SYMBOL;
			
			String jvmReturnDescriptor = parseJVMReturnDescriptor( jvmMDUnparsedAsString );
			
			if ( jvmReturnDescriptor == null )
				throw new IllegalStateException( "Ilformed method descriptor, missing return type." );
			
			jvmMethodDescriptorParsed += jvmReturnDescriptor;
		}

		if ( jvmMethodDescriptorParsed != null )
			interpreter.interpreteJVMMethodDescriptor( jvmMethodDescriptorParsed );
		
		return jvmMethodDescriptorParsed;
	}
	
	private String parseJVMParameterDescriptor( String jvmDescriptor )
	{
		String jvmParameterDescriptor = parseJVMFieldType( jvmDescriptor );

		if ( jvmParameterDescriptor != null )
			interpreter.interpreteJVMParameterDescriptor( jvmParameterDescriptor );
		
		return jvmParameterDescriptor;
	}
	
	private String parseJVMReturnDescriptor( String jvmDescriptor )
	{
		String jvmReturnDescriptor = parseJVMFieldType( jvmDescriptor );

		if ( jvmReturnDescriptor == null )
			jvmReturnDescriptor = parseJVMVoidDescriptor( jvmDescriptor );
		
		if ( jvmReturnDescriptor != null )
			interpreter.interpreteJVMReturnDescriptor( jvmReturnDescriptor );
		
		return jvmReturnDescriptor;
	}
	
	private String parseJVMVoidDescriptor( String jvmDescriptor )
	{
		String jvmVoidDescriptor = null;

		if ( jvmDescriptor.startsWith( VOID_JVM_TYPE_NAME ) )
			jvmVoidDescriptor = VOID_JVM_TYPE_NAME;
			
		if ( jvmVoidDescriptor != null )
			interpreter.interpreteJVMVoidDescriptor( jvmVoidDescriptor );
		
		return jvmVoidDescriptor;
	}
	
	public String parseJVMFieldDescriptor( String jvmDescriptor )
	{
		String jvmFieldDescriptor = parseJVMFieldType( jvmDescriptor );

		if ( jvmFieldDescriptor != null )
			interpreter.interpreteJVMFieldDescriptor( jvmFieldDescriptor );
		
		return jvmFieldDescriptor;
	}
	
	private String parseJVMFieldType( String jvmDescriptor )
	{
		String jvmFieldType = parseJVMBaseType( jvmDescriptor );
		
		if ( jvmFieldType == null )
			jvmFieldType = parseJVMObjectType( jvmDescriptor );
		
		if ( jvmFieldType == null )
			jvmFieldType = parseJVMArrayType( jvmDescriptor );
		
		if ( jvmFieldType != null )
			interpreter.interpreteJVMFieldType( jvmFieldType );
		
		return jvmFieldType;
	}
	
	private String parseJVMBaseType( String jvmDescriptor )
	{
		String jvmBaseType = null;
		
		if ( jvmDescriptor.startsWith( BYTE_JVM_TYPE_NAME ) )
			jvmBaseType = BYTE_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( CHAR_JVM_TYPE_NAME ) )
			jvmBaseType = CHAR_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( SHORT_JVM_TYPE_NAME ) )
			jvmBaseType = SHORT_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( INT_JVM_TYPE_NAME ) )
			jvmBaseType = INT_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( LONG_JVM_TYPE_NAME ) )
			jvmBaseType = LONG_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( FLOAT_JVM_TYPE_NAME ) )
			jvmBaseType = FLOAT_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( DOUBLE_JVM_TYPE_NAME ) )
			jvmBaseType = DOUBLE_JVM_TYPE_NAME;
		else if ( jvmDescriptor.startsWith( BOOLEAN_JVM_TYPE_NAME ) )
			jvmBaseType = BOOLEAN_JVM_TYPE_NAME;

		if ( jvmBaseType != null )
			interpreter.interpreteJVMBaseType( jvmBaseType );
		
		return jvmBaseType;
	}
	
	private String parseJVMObjectType( String jvmDescriptor )
	{
		String jvmObjectType = null;
		
		if ( jvmDescriptor.startsWith( REFERENCE_JVM_TYPE_NAME ) )
		{
			int eoRefSymbolIndex = jvmDescriptor.indexOf( EO_REF_SYMBOL );
			
			if ( eoRefSymbolIndex == -1 )
				throw new IllegalStateException( "Ilformed object type." );
			
			jvmObjectType = jvmDescriptor.substring( 0, eoRefSymbolIndex + 1 );
		}

		if ( jvmObjectType != null )
			interpreter.interpreteJVMObjectType( jvmObjectType );
		
		return jvmObjectType;
	}
	
	private String parseJVMArrayType( String jvmDescriptor )
	{
		String jvmArrayType = null;
		
		if ( jvmDescriptor.startsWith( ARRAY_DIM_SYMBOL ) )
		{
			String jvmComponentType = jvmDescriptor.substring( ARRAY_DIM_SYMBOL.length(), jvmDescriptor.length() );
			jvmArrayType = ARRAY_DIM_SYMBOL + parseJVMComponentType( jvmComponentType );
		}
		
		if ( jvmArrayType != null )
			interpreter.interpreteJVMArrayType( jvmArrayType );
		
		return jvmArrayType;
	}
	
	private String parseJVMComponentType( String jvmDescriptor )
	{
		String jvmComponentType = parseJVMFieldDescriptor( jvmDescriptor );
		
		if ( jvmComponentType != null )
			interpreter.interpreteJVMComponentType( jvmComponentType );
		
		return jvmComponentType;
	}

}
