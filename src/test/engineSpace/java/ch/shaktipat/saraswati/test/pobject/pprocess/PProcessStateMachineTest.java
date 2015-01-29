package ch.shaktipat.saraswati.test.pobject.pprocess;

import static ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine.*;

import org.junit.Test;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;

public class PProcessStateMachineTest
{
	private PersistentProcessStateMachine stateMachine = new PersistentProcessStateMachine();
	
	@Test
	public void testNormalCases()
	{
		stateMachine.resetMachine();
		
		validateState( NEW_STATE );
		validateState( STABLE_STATE );
		
		stateMachine.fireEvent( START_EVENT );
		validateState( RUNNING_STATE );
		
		// Test transitions from RUNNING_STATE.
		
		stateMachine.fireEvent( INVOKE_NON_PERSISTENT_METHOD_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( RETURN_FROM_NON_PERSISTENT_METHOD_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( INVOKE_PERSISTENT_METHOD_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( RETURN_FROM_PERSISTENT_METHOD_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( SET_CHECKPOINT_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( SLEEP_EVENT );
		validateState( SLEEPING_STATE );
		
		stateMachine.fireEvent( SLEEP_COMPLETED_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( JOIN_EVENT );
		validateState( JOINING_STATE );
		
		stateMachine.fireEvent( PROCESS_JOINED_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( LISTEN_EVENT );
		validateState( LISTENING_STATE );
		
		stateMachine.fireEvent( EVENT_RECEIVED_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( POP_SEGMENT_EVENT );
		validateState( CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE );
		
		stateMachine.fireEvent( CONTINUE_WITH_CURRENT_SEGMENT_EVENT );
		validateState( CURRENT_EXECUTION_SEGMENT_PENDING_STATE );
		
		stateMachine.fireEvent( PUSH_SEGMENT_EVENT );
		validateState( CURRENT_EXECUTION_SEGMENT_PENDING_STATE );
		
		// Test deep history with suspension.
		
		stateMachine.fireEvent( SUSPEND_EVENT );
		validateState( SUSPENDED_STATE );
		
		stateMachine.fireEvent( RESUME_EVENT );
		validateState( RUNNING_STATE );
		
		stateMachine.fireEvent( SLEEP_EVENT );
		stateMachine.fireEvent( SUSPEND_EVENT );
		validateState( SUSPENDED_STATE );
		
		stateMachine.fireEvent( RESUME_EVENT );
		validateState( SLEEPING_STATE );

		// Test deep history with passivation.
		
		stateMachine.fireEvent( PASSIVATE_EVENT );
		validateState( PASSIVE_STATE );
		
		stateMachine.fireEvent( ACTIVATE_EVENT );
		validateState( SLEEPING_STATE );
		
		stateMachine.fireEvent( SLEEP_COMPLETED_EVENT );
		stateMachine.fireEvent( PASSIVATE_EVENT );
		validateState( PASSIVE_STATE );
		
		stateMachine.fireEvent( ACTIVATE_EVENT );
		validateState( RUNNING_STATE );
		
		// Now we test "double" deep history with suspension + passivation.
		stateMachine.fireEvent( SUSPEND_EVENT );
		stateMachine.fireEvent( PASSIVATE_EVENT );
		stateMachine.fireEvent( ACTIVATE_EVENT );
		validateState( SUSPENDED_STATE );

		stateMachine.fireEvent( RESUME_EVENT );
		validateState( RUNNING_STATE );
		
		// Test volatility states.
		
		stateMachine.fireEvent( DESTABILIZE_EVENT );
		validateState( VOLATILE_STATE );
		
		stateMachine.fireEvent( STABILIZE_EVENT );
		validateState( STABLE_STATE );
		
		// Test termination and destruction.
		
		stateMachine.fireEvent( POP_SEGMENT_EVENT );
		validateState( CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE );

		stateMachine.fireEvent( PROCESS_COMPLETED_EVENT );
		validateState( TERMINATED_STATE );
		
		stateMachine.fireEvent( DESTROY_EVENT );
		validateState( FINAL_STATE );
	}
	
	@Test
	public void testStop()
	{
		stateMachine.resetMachine();
		
		stateMachine.fireEvent( START_EVENT );
		stateMachine.fireEvent( CANCEL_EVENT );
		validateState( TERMINATED_STATE );
	}
	
	private void validateState( String expectedState )
	{
		if ( !stateMachine.isInState( expectedState ) )
			throw new RuntimeException( "Validation error." );
	}
}
