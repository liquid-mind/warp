package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.model.SCXML;
import org.xml.sax.ErrorHandler;

import ch.shaktipat.exwrapper.org.apache.commons.scxml.io.SCXMLParserWrapper;
import ch.shaktipat.saraswati.internal.common.SaraswatiStateMachine;

public class PersistentProcessStateMachine extends SaraswatiStateMachine
{
	private static final long serialVersionUID = 1L;
	
	private static SCXML stateMachineDescription;
	
	// States
	public static final String	REFERENCABLE_STATE = "referencable";
	public static final String		REFERENCABLE_PARALLEL_STATE = "referencable-parallel";
	public static final String			VOLATILITY_STATE = "volatility";
	public static final String				STABLE_STATE = "stable";
	public static final String				VOLATILE_STATE = "volatile";
	public static final String			EXECUTION_STATE = "execution";
	public static final String				ACTIVE_STATE = "active";
	public static final String					NEW_STATE = "new";
	public static final String					PENDING_STATE = "pending";
	public static final String						ANIMATED_STATE = "animated";
	public static final String							RUNNING_STATE = "running";
	public static final String								CURRENT_EXECUTION_SEGMENT_PENDING_STATE = "current-execution-segment-pending";
	public static final String								CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE = "current-execution-segment-terminated";
	public static final String							WAITING_STATE = "waiting";
	public static final String								SLEEPING_STATE = "sleeping";
	public static final String								JOINING_STATE = "joining";
	public static final String								LISTENING_STATE = "listening";
	public static final String						SUSPENDED_STATE = "suspended";
	public static final String					TERMINATED_STATE = "terminated";
	public static final String						COMPLETED_STATE = "completed";
	public static final String						THREW_EXCEPTION_STATE = "threw-exception";
	public static final String						CANCELLED_STATE = "cancelled";
	public static final String				PASSIVE_STATE = "passive";
	public static final String	FINAL_STATE = "final";

	// Events
	public static final String ACTIVATE_EVENT = "activate";
	public static final String DESTABILIZE_EVENT = "destabilize";
	public static final String DESTROY_EVENT = "destroy";
	public static final String EVENT_RECEIVED_EVENT = "event-received";
	public static final String INVOKE_NON_PERSISTENT_METHOD_EVENT = "invoke-non-persistent-method";
	public static final String INVOKE_PERSISTENT_METHOD_EVENT = "invoke-persistent-method";
	public static final String JOIN_EVENT = "join";
	public static final String LISTEN_EVENT = "listen";
	public static final String PASSIVATE_EVENT = "passivate";
	public static final String CONTINUE_WITH_CURRENT_SEGMENT_EVENT = "continue-with-current-segment";
	public static final String PROCESS_JOINED_EVENT = "process-joined";
	public static final String PUSH_SEGMENT_EVENT = "push-segment";
	public static final String RESUME_EVENT = "resume";
	public static final String RETURN_FROM_NON_PERSISTENT_METHOD_EVENT = "return-from-non-persistent-method";
	public static final String RETURN_FROM_PERSISTENT_METHOD_EVENT = "return-from-persistent-method";
	public static final String PROCESS_COMPLETED_EVENT = "process-completed";
	public static final String PROCESS_THREW_EXCEPTION_EVENT = "process-threw-exception";
	public static final String POP_SEGMENT_EVENT = "pop-segment";
	public static final String SET_CHECKPOINT_EVENT = "set-checkpoint";
	public static final String SLEEP_EVENT = "sleep";
	public static final String SLEEP_COMPLETED_EVENT = "sleep-completed";
	public static final String STABILIZE_EVENT = "stabilize";
	public static final String START_EVENT = "start";
	public static final String CANCEL_EVENT = "cancel";
	public static final String SUSPEND_EVENT = "suspend";

	// Originally, eventToStatesMap was declared final since it is a constant value, however,
	// the JVM demonstrated very peculiar behavior: when loading the class in the context of
	// object deserialization (i.e., after stopping and starting the VM and then loading a
	// persistent process) the static initializer was not invoked. Also, getEventToStatesMap()
	// seemed was (later) not invoked.
	// TODO: to be safe, should probably redefine all final static fields in all serializable
	// classes as non-final (though I haven't had problems anywhere else yet).
	private static Map< String, BeforeAndAfterStates > eventToStatesMap = new HashMap< String, BeforeAndAfterStates >();
	
	static
	{
		eventToStatesMap.put( ACTIVATE_EVENT, new BeforeAndAfterStates( new BeforeStates( PASSIVE_STATE ), new AfterStates( ACTIVE_STATE ) ) );
		eventToStatesMap.put( DESTABILIZE_EVENT, new BeforeAndAfterStates( new BeforeStates( STABLE_STATE ), new AfterStates( VOLATILE_STATE ) ) );
		eventToStatesMap.put( DESTROY_EVENT, new BeforeAndAfterStates( new BeforeStates( STABLE_STATE, TERMINATED_STATE ), new AfterStates( FINAL_STATE ) ) );
		eventToStatesMap.put( EVENT_RECEIVED_EVENT, new BeforeAndAfterStates( new BeforeStates( LISTENING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( INVOKE_NON_PERSISTENT_METHOD_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( INVOKE_PERSISTENT_METHOD_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( JOIN_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( JOINING_STATE ) ) );
		eventToStatesMap.put( LISTEN_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( LISTENING_STATE ) ) );
		eventToStatesMap.put( PASSIVATE_EVENT, new BeforeAndAfterStates( new BeforeStates( ACTIVE_STATE ), new AfterStates( PASSIVE_STATE ) ) );
		eventToStatesMap.put( CONTINUE_WITH_CURRENT_SEGMENT_EVENT, new BeforeAndAfterStates( new BeforeStates( CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE ), new AfterStates( CURRENT_EXECUTION_SEGMENT_PENDING_STATE ) ) );
		eventToStatesMap.put( PROCESS_JOINED_EVENT, new BeforeAndAfterStates( new BeforeStates( JOINING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( PUSH_SEGMENT_EVENT, new BeforeAndAfterStates( new BeforeStates( CURRENT_EXECUTION_SEGMENT_PENDING_STATE ), new AfterStates( CURRENT_EXECUTION_SEGMENT_PENDING_STATE ) ) );
		eventToStatesMap.put( RESUME_EVENT, new BeforeAndAfterStates( new BeforeStates( SUSPENDED_STATE ), new AfterStates( ANIMATED_STATE ) ) );
		eventToStatesMap.put( RETURN_FROM_NON_PERSISTENT_METHOD_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( RETURN_FROM_PERSISTENT_METHOD_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( PROCESS_COMPLETED_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( COMPLETED_STATE ) ) );
		eventToStatesMap.put( PROCESS_THREW_EXCEPTION_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( THREW_EXCEPTION_STATE ) ) );
		eventToStatesMap.put( POP_SEGMENT_EVENT, new BeforeAndAfterStates( new BeforeStates( CURRENT_EXECUTION_SEGMENT_PENDING_STATE ), new AfterStates( CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE ) ) );
		eventToStatesMap.put( SET_CHECKPOINT_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( SLEEP_EVENT, new BeforeAndAfterStates( new BeforeStates( RUNNING_STATE ), new AfterStates( SLEEPING_STATE ) ) );
		eventToStatesMap.put( SLEEP_COMPLETED_EVENT, new BeforeAndAfterStates( new BeforeStates( SLEEPING_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( STABILIZE_EVENT, new BeforeAndAfterStates( new BeforeStates( VOLATILE_STATE ), new AfterStates( STABLE_STATE ) ) );
		eventToStatesMap.put( START_EVENT, new BeforeAndAfterStates( new BeforeStates( NEW_STATE ), new AfterStates( RUNNING_STATE ) ) );
		eventToStatesMap.put( CANCEL_EVENT, new BeforeAndAfterStates( new BeforeStates( PENDING_STATE ), new AfterStates( CANCELLED_STATE ) ) );
		eventToStatesMap.put( SUSPEND_EVENT, new BeforeAndAfterStates( new BeforeStates( ANIMATED_STATE ), new AfterStates( SUSPENDED_STATE ) ) );
	}
	
	public PersistentProcessStateMachine()	
	{
		super( getStateMachineDescription() );
	}
	
	private synchronized static SCXML getStateMachineDescription()
	{
		if ( stateMachineDescription == null )
		{
			URL url = Thread.currentThread().getContextClassLoader().getResource( "ch/shaktipat/saraswati/internal/execution/persistent-process-state-machine.xml" );
			ErrorHandler errHandler = new SimpleErrorHandler();
			stateMachineDescription = SCXMLParserWrapper.parse( url, errHandler );
		}

		return stateMachineDescription;
	}

	@Override
	protected Map< String, BeforeAndAfterStates > getEventToStatesMap()
	{
		return eventToStatesMap;
	}
}
