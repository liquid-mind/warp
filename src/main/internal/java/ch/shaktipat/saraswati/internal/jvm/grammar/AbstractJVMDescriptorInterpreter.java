package ch.shaktipat.saraswati.internal.jvm.grammar;


public abstract class AbstractJVMDescriptorInterpreter implements JVMDescriptorInterpreter
{

	@Override
	public void interpreteJVMMethodDescriptor( String jvmMethodDescriptor ) {}

	@Override
	public void interpreteJVMParameterDescriptor( String jvmParameterDescriptor ) {}

	@Override
	public void interpreteJVMReturnDescriptor( String jvmReturnDescriptor ) {}

	@Override
	public void interpreteJVMVoidDescriptor( String jvmVoidDescriptor ) {}

	@Override
	public void interpreteJVMFieldDescriptor( String jvmFieldDescriptor ) {}

	@Override
	public void interpreteJVMFieldType( String jvmFieldType ) {}

	@Override
	public void interpreteJVMBaseType( String jvmBaseType ) {}

	@Override
	public void interpreteJVMObjectType( String jvmObjectType ) {}

	@Override
	public void interpreteJVMArrayType( String jvmArrayType ) {}

	@Override
	public void interpreteJVMComponentType( String jvmComponentType ) {}
	
}
