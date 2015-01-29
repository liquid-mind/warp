package ch.shaktipat.saraswati.internal.common;

public class FieldAndMethodConstants
{
	// Methods - Basic Type Values
	public static final String BYTE_VALUE_METHOD = "byteValue";
	public static final String CHAR_VALUE_METHOD = "charValue";
	public static final String SHORT_VALUE_METHOD = "shortValue";
	public static final String INT_VALUE_METHOD = "intValue";
	public static final String LONG_VALUE_METHOD = "longValue";
	public static final String FLOAT_VALUE_METHOD = "floatValue";
	public static final String DOUBLE_VALUE_METHOD = "doubleValue";
	public static final String BOOLEAN_VALUE_METHOD = "booleanValue";

	// Methods - Misc
	public static final String GET_PPROCESS_PMETHOD_COMMUNICATION_AREA_OF_CURRENT_PROCESS_METHOD = "getPProcessPMethodCommunicationAreaOfCurrentProcess";
	public static final String SET_LINE_NUMBER_OF_CURRENT_PROCESS_METHOD = "setLineNumberOfCurrentProcess";
	public static final String GET_CURRENT_PFRAME_OF_CURRENT_PROCESS_METHOD = "getCurrentPFrameOfCurrentProcess";
	public static final String GET_CURRENT_PROCESS_OID_METHOD = "getCurrentProcessOID";
	public static final String GET_EXECUTION_STATE_METHOD = "getExecutionState";
	public static final String GET_STACK_TRACE_ELEMENT_METHOD = "getStackTraceElement";
	public static final String GET_INVOKED_METHOD_NAME_METHOD = "getInvokedMethodName";
	public static final String GET_IS_CONTAINER_INVOCATION_METHOD = "getIsContainerInvocation";
	public static final String SET_IS_CONTAINER_INVOCATION_METHOD = "setIsContainerInvocation";
	public static final String VALUE_OF_METHOD = "valueOf";
	public static final String SET_JVM_CLASS_NAME_METHOD = "setJVMClassName";
	public static final String SET_JVM_METHOD_NAME_METHOD = "setJVMMethodName";
	public static final String SET_JVM_METHOD_DESCRIPTOR_METHOD = "setJVMMethodDescriptor";
	public static final String SET_METHOD_INVOCATION_TYPE_METHOD = "setMethodInvocationType";
	public static final String SET_LOCAL_VARIABLES_METHOD = "setLocalVariables";
	public static final String SET_OPERAND_STACK_METHOD = "setOperandStack";
	public static final String SET_PROGRAM_COUNTER_METHOD = "setProgramCounter";
	public static final String GET_LOCAL_VARIABLES_METHOD = "getLocalVariables";
	public static final String GET_OPERAND_STACK_METHOD = "getOperandStack";
	public static final String GET_PROGRAM_COUNTER_METHOD = "getProgramCounter";
	public static final String GET_EXCEPTION_OCCURED_METHOD = "getExceptionOccured";
	public static final String MAIN_METHOD = "main";
	public static final String RUN_METHOD = "run";
	public static final String CALL_METHOD = "call";
	public static final String LISTEN_METHOD = "listen";
	public static final String JOIN_METHOD = "join";
	public static final String SLEEP_METHOD = "sleep";
	public static final String INVOKE_METHOD = "invoke";
	public static final String GET_OID_METHOD = "getOID";
	public static final String GET_FUTURE_METHOD = "getFuture";
	public static final String EQUALS_METHOD = "equals";
	public static final String HASH_CODE_METHOD = "hashCode";
	public static final String TO_STRING_METHOD = "toString";
	public static final String EXISTS_METHOD = "exists";
	public static final String DESTROY_METHOD = "destroy";
	public static final String GET_METHOD = "get";

	// Special methods
	public static final String INIT_METHOD = "<init>";
	public static final String CLINIT_METHOD = "<clinit>";
	
	// Saraswati methods
	public static final String SARASWATI_PREFIX = "__saraswati_";
	public static final String SARASWATI_INIT_METHOD = SARASWATI_PREFIX + "init";
	public static final String SARASWATI_CLINIT_METHOD = SARASWATI_PREFIX + "clinit";
	public static final String SARASWATI_NEW_MARKER_METHOD = SARASWATI_PREFIX + "new_marker";
	public static final String SARASWATI_GET_LOOKUP_METHOD = SARASWATI_PREFIX + "getLookup";
	public static final String SARASWATI_FORCE_STATIC_INITIALIZATION_METHOD = SARASWATI_PREFIX + "forceStaticInitialization";

	// Fields
	public static final String LINE_NUMBER_FIELD = "lineNumber";
}
