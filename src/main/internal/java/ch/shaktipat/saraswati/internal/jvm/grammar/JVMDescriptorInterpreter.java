package ch.shaktipat.saraswati.internal.jvm.grammar;

public interface JVMDescriptorInterpreter
{
	public void interpreteJVMMethodDescriptor( String jvmMethodDescriptor );
	public void interpreteJVMParameterDescriptor( String jvmParameterDescriptor );
	public void interpreteJVMReturnDescriptor( String jvmReturnDescriptor );
	public void interpreteJVMVoidDescriptor( String jvmVoidDescriptor );
	public void interpreteJVMFieldDescriptor( String jvmFieldDescriptor );
	public void interpreteJVMFieldType( String jvmFieldType );
	public void interpreteJVMBaseType( String jvmBaseType );
	public void interpreteJVMObjectType( String jvmObjectType );
	public void interpreteJVMArrayType( String jvmArrayType );
	public void interpreteJVMComponentType( String jvmComponentType );
}
