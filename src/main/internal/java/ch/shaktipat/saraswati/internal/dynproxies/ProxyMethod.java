package ch.shaktipat.saraswati.internal.dynproxies;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;

@Retention( RetentionPolicy.RUNTIME )
public @interface ProxyMethod
{
	public enum Synchronicity
	{
		SYNCHRONOUS,
		ASYNCHRONOUS
	};
	
	public enum StateProtection
	{
		STATE_READ_LOCK,
		STATE_WRITE_LOCK,
		NONE
	};
	
	public enum ExecutionProtection
	{
		EXECUTION_READ_LOCK,
		EXECUTION_WRITE_LOCK,
		NONE
	};
	
	public Synchronicity synchronicity();
	public StateProtection stateProtection() default StateProtection.NONE;
	public ExecutionProtection executionStateProtection() default ExecutionProtection.NONE;
	public String[] validProcessStates() default PersistentProcessStateMachine.REFERENCABLE_STATE;
}
