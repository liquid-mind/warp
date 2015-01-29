package ch.shaktipat.saraswati.pobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.shaktipat.saraswati.common.AbstractEvent;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;

public class PProcessEvent extends AbstractEvent
{
	private static final long serialVersionUID = 1L;

	// States
	public static final String	EXECUTION_STATE = PersistentProcessStateMachine.EXECUTION_STATE;
	public static final String	ACTIVE_STATE = PersistentProcessStateMachine.ACTIVE_STATE;
	public static final String	NEW_STATE = PersistentProcessStateMachine.NEW_STATE;
	public static final String	PENDING_STATE = PersistentProcessStateMachine.PENDING_STATE;
	public static final String	ANIMATED_STATE = PersistentProcessStateMachine.ANIMATED_STATE;
	public static final String	RUNNING_STATE = PersistentProcessStateMachine.RUNNING_STATE;
	public static final String	WAITING_STATE = PersistentProcessStateMachine.WAITING_STATE;
	public static final String	SLEEPING_STATE = PersistentProcessStateMachine.SLEEPING_STATE;
	public static final String	JOINING_STATE = PersistentProcessStateMachine.JOINING_STATE;
	public static final String	LISTENING_STATE = PersistentProcessStateMachine.LISTENING_STATE;
	public static final String	SUSPENDED_STATE = PersistentProcessStateMachine.SUSPENDED_STATE;
	public static final String	TERMINATED_STATE = PersistentProcessStateMachine.TERMINATED_STATE;
	public static final String	COMPLETED_STATE = PersistentProcessStateMachine.COMPLETED_STATE;
	public static final String	THREW_EXCEPTION_STATE = PersistentProcessStateMachine.THREW_EXCEPTION_STATE;
	public static final String	CANCELLED_STATE = PersistentProcessStateMachine.CANCELLED_STATE;
	public static final String	PASSIVE_STATE = PersistentProcessStateMachine.PASSIVE_STATE;
	public static final String	FINAL_STATE = PersistentProcessStateMachine.FINAL_STATE;

	// Events
	public static final String ACTIVATE_EVENT = PersistentProcessStateMachine.ACTIVATE_EVENT;
	public static final String DESTROY_EVENT = PersistentProcessStateMachine.DESTROY_EVENT;
	public static final String EVENT_RECEIVED_EVENT = PersistentProcessStateMachine.EVENT_RECEIVED_EVENT;
	public static final String JOIN_EVENT = PersistentProcessStateMachine.JOIN_EVENT;
	public static final String LISTEN_EVENT = PersistentProcessStateMachine.LISTEN_EVENT;
	public static final String PASSIVATE_EVENT = PersistentProcessStateMachine.PASSIVATE_EVENT;
	public static final String PROCESS_JOINED_EVENT = PersistentProcessStateMachine.PROCESS_JOINED_EVENT;
	public static final String RESUME_EVENT = PersistentProcessStateMachine.RESUME_EVENT;
	public static final String PROCESS_COMPLETED_EVENT = PersistentProcessStateMachine.PROCESS_COMPLETED_EVENT;
	public static final String PROCESS_THREW_EXCEPTION_EVENT = PersistentProcessStateMachine.PROCESS_THREW_EXCEPTION_EVENT;
	public static final String SET_CHECKPOINT_EVENT = PersistentProcessStateMachine.SET_CHECKPOINT_EVENT;
	public static final String SLEEP_EVENT = PersistentProcessStateMachine.SLEEP_EVENT;
	public static final String SLEEP_COMPLETED_EVENT = PersistentProcessStateMachine.SLEEP_COMPLETED_EVENT;
	public static final String START_EVENT = PersistentProcessStateMachine.START_EVENT;
	public static final String CANCEL_EVENT = PersistentProcessStateMachine.CANCEL_EVENT;
	public static final String SUSPEND_EVENT = PersistentProcessStateMachine.SUSPEND_EVENT;
	
	public static final String ENTERED_PROCESS_STATE = "ENTERED_PROCESS_STATE";
	public static final String EXITED_PROCESS_STATE = "EXITED_PROCESS_STATE";
	public static final String EVENT_OCCURED = "EVENT_OCCURED";
	
	private static final Set< String > USER_RELEVANT_STATES = new HashSet< String >();
	private static final Set< String > USER_RELEVANT_EVENTS = new HashSet< String >();
	
	static
	{
		USER_RELEVANT_STATES.add( EXECUTION_STATE );
		USER_RELEVANT_STATES.add( ACTIVE_STATE );
		USER_RELEVANT_STATES.add( NEW_STATE );
		USER_RELEVANT_STATES.add( PENDING_STATE );
		USER_RELEVANT_STATES.add( ANIMATED_STATE );
		USER_RELEVANT_STATES.add( RUNNING_STATE );
		USER_RELEVANT_STATES.add( WAITING_STATE );
		USER_RELEVANT_STATES.add( SLEEPING_STATE );
		USER_RELEVANT_STATES.add( JOINING_STATE );
		USER_RELEVANT_STATES.add( LISTENING_STATE );
		USER_RELEVANT_STATES.add( SUSPENDED_STATE );
		USER_RELEVANT_STATES.add( TERMINATED_STATE );
		USER_RELEVANT_STATES.add( COMPLETED_STATE );
		USER_RELEVANT_STATES.add( THREW_EXCEPTION_STATE );
		USER_RELEVANT_STATES.add( CANCELLED_STATE );
		USER_RELEVANT_STATES.add( PASSIVE_STATE );
		USER_RELEVANT_STATES.add( FINAL_STATE );
		
		USER_RELEVANT_EVENTS.add( ACTIVATE_EVENT );
		USER_RELEVANT_EVENTS.add( DESTROY_EVENT );
		USER_RELEVANT_EVENTS.add( EVENT_RECEIVED_EVENT );
		USER_RELEVANT_EVENTS.add( JOIN_EVENT );
		USER_RELEVANT_EVENTS.add( LISTEN_EVENT );
		USER_RELEVANT_EVENTS.add( PASSIVATE_EVENT );
		USER_RELEVANT_EVENTS.add( PROCESS_JOINED_EVENT );
		USER_RELEVANT_EVENTS.add( RESUME_EVENT );
		USER_RELEVANT_EVENTS.add( PROCESS_COMPLETED_EVENT );
		USER_RELEVANT_EVENTS.add( PROCESS_THREW_EXCEPTION_EVENT );
		USER_RELEVANT_EVENTS.add( SET_CHECKPOINT_EVENT );
		USER_RELEVANT_EVENTS.add( SLEEP_EVENT );
		USER_RELEVANT_EVENTS.add( SLEEP_COMPLETED_EVENT );
		USER_RELEVANT_EVENTS.add( START_EVENT );
		USER_RELEVANT_EVENTS.add( CANCEL_EVENT );
		USER_RELEVANT_EVENTS.add( SUSPEND_EVENT );
	}
	
	public PProcessEvent( String propertyName, String propertyValue )
	{
		setProperty( propertyName, propertyValue );
	}
	
	public static List< PProcessEvent > determineEvents( String[] statesBefore, String[] statesAfter, String event )
	{
		// Determine user relevant states from raw states.
		List< String > statesBeforeUserRelevant = new ArrayList< String >( Arrays.asList( statesBefore ) );
		statesBeforeUserRelevant.retainAll( USER_RELEVANT_STATES );
		List< String > statesAfterUserRelevant = new ArrayList< String >( Arrays.asList( statesAfter ) );
		statesAfterUserRelevant.retainAll( USER_RELEVANT_STATES );
		
		// Determine which states have actually changed.
		List< String > statesExited = new ArrayList< String >( statesBeforeUserRelevant );
		statesExited.removeAll( statesAfterUserRelevant );
		List< String > statesEntered = new ArrayList< String >( statesAfterUserRelevant );
		statesEntered.removeAll( statesBeforeUserRelevant );
		
		// Create list of PProcessEvents.
		List< PProcessEvent > pProcessEvents = new ArrayList< PProcessEvent >();
		
		if ( USER_RELEVANT_EVENTS.contains( event ) )
			pProcessEvents.add( new PProcessEvent( EVENT_OCCURED, event ) );

		for ( String stateExited : statesExited )
			pProcessEvents.add( new PProcessEvent( EXITED_PROCESS_STATE, stateExited ) );

		for ( String stateEntered : statesEntered )
			pProcessEvents.add( new PProcessEvent( ENTERED_PROCESS_STATE, stateEntered ) );
		
		ensureTerminatedStateEventAtEnd( pProcessEvents );
		
		return pProcessEvents;
	}
	
	// It is critical to ensure that the TERMINATED_STATE is always the last event to be
	// communicated. The reason is that join() uses TERMINATED_STATE to decide when the
	// process has completed and once join() returns the process may be destroyed at any
	// time. When the process is destroyed, any composite parts are destroyed with it
	// (in fact, they are destoryed *before* it). Without this precaution there could be
	// a race condition between the destruction of the process's topic and the
	// sending of events.
	private static void ensureTerminatedStateEventAtEnd( List< PProcessEvent > pProcessEvents )
	{
		for ( int i = 0 ; i < pProcessEvents.size() ; ++i )
		{
			String enteredProcessState = pProcessEvents.get( i ).getProperty( ENTERED_PROCESS_STATE );
			
			if ( enteredProcessState != null && enteredProcessState.equals( TERMINATED_STATE ) )
			{
				pProcessEvents.add( pProcessEvents.remove( i ) );
				break;
			}
		}
	}
}

