package ch.shaktipat.saraswati.internal.pobject;

import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.ExecutionProtection.EXECUTION_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.ExecutionProtection.EXECUTION_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_READ_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.StateProtection.STATE_WRITE_LOCK;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.ASYNCHRONOUS;
import static ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod.Synchronicity.SYNCHRONOUS;
import static ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine.NEW_STATE;
import static ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine.PENDING_STATE;
import static ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine.RUNNING_STATE;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.lang.ArrayUtils;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.lang.InstantiationExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.FieldWrapper;
import ch.shaktipat.saraswati.common.DeadlineSpecification;
import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeAndUnitSpecification;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;
import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.common.SaraswatiInternalError;
import ch.shaktipat.saraswati.internal.dynproxies.PersistentProcessInvocationHandler;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyMethod;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.instrument.SymbolRegistry;
import ch.shaktipat.saraswati.internal.instrument.__saraswati_dummy_type;
import ch.shaktipat.saraswati.internal.instrument.method.MethodInvocationType;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;
import ch.shaktipat.saraswati.internal.jvm.grammar.JVMDescriptorHelper;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectListHelper;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.ExecutionState;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.IntegralHelper;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.MethodInvocationInstance;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.MethodResultInstance;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PApplicationLogHandler;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PFrame;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PFrameStack;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegment;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegmentResult;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessPMethodCommunicationArea;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PSystemLogHandler;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.ProcessExecutionException;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.scheduler.TimeoutEvent;
import ch.shaktipat.saraswati.internal.test.ConcurrentTestingHelper;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.POwnableObject;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessEvent;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.VolatileResource;
import ch.shaktipat.saraswati.volatility.Volatility;
import ch.shaktipat.saraswati.volatility.VolatilityException;

public class PersistentProcessImpl extends PersistentObjectImpl implements PersistentProcess, PProcessHandle, PProcess
{
	private static final long serialVersionUID = 1L;

	private static ThreadLocal< PProcessOID > currentProcessOID = new ThreadLocal< PProcessOID >();
	
	private static final Method RUN_METHOD = ClassWrapper.getDeclaredMethod( Runnable.class, FieldAndMethodConstants.RUN_METHOD, new Class[] {} );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN1_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN2_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN3_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Date.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN4_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN5_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN6_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class, Date.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN7_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN8_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN9_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, Date.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN10_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN11_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PEVENT_QUEUE_LISTEN12_METHOD_NAME = new FQJVMMethodName( PEventQueue.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class, Date.class );
	private static final FQJVMMethodName PPROCESS_LISTEN1_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class );
	private static final FQJVMMethodName PPROCESS_LISTEN2_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_LISTEN3_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Date.class );
	private static final FQJVMMethodName PPROCESS_LISTEN4_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class );
	private static final FQJVMMethodName PPROCESS_LISTEN5_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_LISTEN6_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, String.class, Date.class );
	private static final FQJVMMethodName PPROCESS_LISTEN7_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class );
	private static final FQJVMMethodName PPROCESS_LISTEN8_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_LISTEN9_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, Date.class );
	private static final FQJVMMethodName PPROCESS_LISTEN10_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class );
	private static final FQJVMMethodName PPROCESS_LISTEN11_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_LISTEN12_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.LISTEN_METHOD, Event.class, Volatility.class, String.class, Date.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN1_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN2_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN3_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class, Date.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN4_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class, Volatility.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN5_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class, Volatility.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_HANDLE_JOIN6_METHOD_NAME = new FQJVMMethodName( PProcessHandle.class, FieldAndMethodConstants.JOIN_METHOD, void.class, Volatility.class, Date.class );
	private static final FQJVMMethodName PPROCESS_SLEEP1_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class );
	private static final FQJVMMethodName PPROCESS_SLEEP2_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_SLEEP3_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class, Date.class );
	private static final FQJVMMethodName PPROCESS_SLEEP4_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class, Volatility.class );
	private static final FQJVMMethodName PPROCESS_SLEEP5_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class, Volatility.class, long.class, TimeUnit.class );
	private static final FQJVMMethodName PPROCESS_SLEEP6_METHOD_NAME = new FQJVMMethodName( PProcess.class, FieldAndMethodConstants.SLEEP_METHOD, void.class, Volatility.class, Date.class );
	private static final FQJVMMethodName INVOKE_METHOD_NAME = new FQJVMMethodName( Method.class, FieldAndMethodConstants.INVOKE_METHOD, Object.class, Object[].class );
	private static final Set< FQJVMMethodName > SPECIAL_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > LISTEN_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > JOIN_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > SLEEP_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > TIME_AND_TIMEUNIT_SIGNATURE_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > TIME_AND_TIMEUNIT_SIGNATURE_METHODS1 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > TIME_AND_TIMEUNIT_SIGNATURE_METHODS2 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > TIME_AND_TIMEUNIT_SIGNATURE_METHODS3 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > DEADLINE_SIGNATURE_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > DEADLINE_SIGNATURE_METHODS1 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > DEADLINE_SIGNATURE_METHODS2 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > DEADLINE_SIGNATURE_METHODS3 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > FILTER_SIGNATURE_METHODS = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > FILTER_SIGNATURE_METHODS1 = new HashSet< FQJVMMethodName >();
	private static final Set< FQJVMMethodName > FILTER_SIGNATURE_METHODS2 = new HashSet< FQJVMMethodName >();

	public static final String PPROCESS_SYSTEM_LOGGER = "PProcessSystemLogger";
	public static final String PPROCESS_APPLICATION_LOGGER = "PProcessApplicationLogger";
	
	// TODO move all public static fields to interface and all public static methods to manager class.
	// TODO need to treat Throwable.fillInStackTrace() as a special method (should fill in persistent stack trace).

	static
	{
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN1_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN2_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN3_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN4_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN5_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN6_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN7_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN8_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN9_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN10_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN11_METHOD_NAME );
		LISTEN_METHODS.add( PEVENT_QUEUE_LISTEN12_METHOD_NAME );
		
		LISTEN_METHODS.add( PPROCESS_LISTEN1_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN2_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN3_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN4_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN5_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN6_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN7_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN8_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN9_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN10_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN11_METHOD_NAME );
		LISTEN_METHODS.add( PPROCESS_LISTEN12_METHOD_NAME );
		
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN1_METHOD_NAME );
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN2_METHOD_NAME );
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN3_METHOD_NAME );
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN4_METHOD_NAME );
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN5_METHOD_NAME );
		JOIN_METHODS.add( PPROCESS_HANDLE_JOIN6_METHOD_NAME );

		SLEEP_METHODS.add( PPROCESS_SLEEP1_METHOD_NAME );
		SLEEP_METHODS.add( PPROCESS_SLEEP2_METHOD_NAME );
		SLEEP_METHODS.add( PPROCESS_SLEEP3_METHOD_NAME );
		SLEEP_METHODS.add( PPROCESS_SLEEP4_METHOD_NAME );
		SLEEP_METHODS.add( PPROCESS_SLEEP5_METHOD_NAME );
		SLEEP_METHODS.add( PPROCESS_SLEEP6_METHOD_NAME );
	
		SPECIAL_METHODS.addAll( LISTEN_METHODS );
		SPECIAL_METHODS.addAll( JOIN_METHODS );
		SPECIAL_METHODS.addAll( SLEEP_METHODS );
		SPECIAL_METHODS.add( INVOKE_METHOD_NAME );
		
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS1.add( PEVENT_QUEUE_LISTEN2_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS1.add( PPROCESS_LISTEN2_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS1.add( PPROCESS_HANDLE_JOIN2_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS1.add( PPROCESS_SLEEP2_METHOD_NAME );
		
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN5_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN8_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PPROCESS_LISTEN5_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PPROCESS_LISTEN8_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PPROCESS_HANDLE_JOIN5_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.add( PPROCESS_SLEEP5_METHOD_NAME );

		TIME_AND_TIMEUNIT_SIGNATURE_METHODS3.add( PEVENT_QUEUE_LISTEN11_METHOD_NAME );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS3.add( PPROCESS_LISTEN11_METHOD_NAME );
		
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS.addAll( TIME_AND_TIMEUNIT_SIGNATURE_METHODS1 );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS.addAll( TIME_AND_TIMEUNIT_SIGNATURE_METHODS2 );
		TIME_AND_TIMEUNIT_SIGNATURE_METHODS.addAll( TIME_AND_TIMEUNIT_SIGNATURE_METHODS3 );
		
		DEADLINE_SIGNATURE_METHODS1.add( PEVENT_QUEUE_LISTEN3_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS1.add( PPROCESS_LISTEN3_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS1.add( PPROCESS_HANDLE_JOIN3_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS1.add( PPROCESS_SLEEP3_METHOD_NAME );

		DEADLINE_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN6_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN9_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS2.add( PPROCESS_LISTEN6_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS2.add( PPROCESS_LISTEN9_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS2.add( PPROCESS_HANDLE_JOIN6_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS2.add( PPROCESS_SLEEP6_METHOD_NAME );

		DEADLINE_SIGNATURE_METHODS3.add( PEVENT_QUEUE_LISTEN12_METHOD_NAME );
		DEADLINE_SIGNATURE_METHODS3.add( PPROCESS_LISTEN12_METHOD_NAME );
		
		DEADLINE_SIGNATURE_METHODS.addAll( DEADLINE_SIGNATURE_METHODS1 );
		DEADLINE_SIGNATURE_METHODS.addAll( DEADLINE_SIGNATURE_METHODS2 );
		DEADLINE_SIGNATURE_METHODS.addAll( DEADLINE_SIGNATURE_METHODS3 );
		
		FILTER_SIGNATURE_METHODS1.add( PEVENT_QUEUE_LISTEN4_METHOD_NAME );
		FILTER_SIGNATURE_METHODS1.add( PEVENT_QUEUE_LISTEN5_METHOD_NAME );
		FILTER_SIGNATURE_METHODS1.add( PEVENT_QUEUE_LISTEN6_METHOD_NAME );
		FILTER_SIGNATURE_METHODS1.add( PPROCESS_LISTEN4_METHOD_NAME );
		FILTER_SIGNATURE_METHODS1.add( PPROCESS_LISTEN5_METHOD_NAME );
		FILTER_SIGNATURE_METHODS1.add( PPROCESS_LISTEN6_METHOD_NAME );

		FILTER_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN10_METHOD_NAME );
		FILTER_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN11_METHOD_NAME );
		FILTER_SIGNATURE_METHODS2.add( PEVENT_QUEUE_LISTEN12_METHOD_NAME );
		FILTER_SIGNATURE_METHODS2.add( PPROCESS_LISTEN10_METHOD_NAME );
		FILTER_SIGNATURE_METHODS2.add( PPROCESS_LISTEN11_METHOD_NAME );
		FILTER_SIGNATURE_METHODS2.add( PPROCESS_LISTEN12_METHOD_NAME );
		
		FILTER_SIGNATURE_METHODS.addAll( FILTER_SIGNATURE_METHODS1 );
		FILTER_SIGNATURE_METHODS.addAll( FILTER_SIGNATURE_METHODS2 );
	}
	
	public enum VolatilityChangeType { INITIAL, PUSH_FRAME, POP_FRAME, ENLIST_RESOURCE, DEENLIST_RESOURCE };

	public static final boolean AUTO_CHECKPOINTING_DEFAULT = true;
	public static final boolean DESTROY_ON_COMPLETION_DEFAULT = false;
	public static final String DEFAULT_PEVENT_QUEUE_NAME = "default event queue";
	public static final String DEFAULT_PEVENT_TOPIC_NAME = "default event topic";

	// Externally inaccessible
	private ReentrantReadWriteLock executionStateLock;
	private ReadLock executionStateReadLock;
	private WriteLock executionStateWriteLock;
	private PEventQueue eventQueue;
	private PEventTopic eventTopic;
	private Event eventReceivedDuringSuspension;
	
	// Protected by: executionStateLock.
	private Stack< PProcessExecutionSegment > pProcessExecutionSegmentStack;
	private PProcessExecutionSegmentResult pProcessExecutionSegmentResult;
	private Runnable runnable;
	private Callable< ? > callable;
	
	// Protected by: stateLock
	private PProcessOID parentOID;
	private Set< PProcessOID > childOIDs;
	private Principal owningPrincipal;
	private Set< POwnableObjectOID > ownableObjectOIDs;
	private boolean autoCheckpointing;
	private boolean destroyOnCompletion;
	private PersistentProcessStateMachine persistentProcessStateMachine;
	private boolean requestSuspension;
	private boolean requestCancellation;
	private String systemLog;
	private String applicationLog;
	private Volatility initialVolatility;
	private Volatility initialVolatilityActual;
	private Set< VolatileResource > volatileResources;
	private List< PProcessEvent > pProcessEventHistory;
	
	// TODO Do we really need initial volatility? The run()/call() methods provide an entry
	// point for the process and can be declared with a particular volatility. If, on the
	// other hand, the volatility of the invoking process needs to be altered, this could
	// be done via volatile resources.
	public PersistentProcessImpl( Runnable runnable, Callable< ? > callable, String name, Volatility initialVolatility, Principal owningPrincipal, boolean autoCheckpointing, boolean destroyOnCompletion )
	{
		super( name, true, true );
		this.runnable = runnable;
		this.callable = callable;
		this.autoCheckpointing = autoCheckpointing;
		this.destroyOnCompletion = destroyOnCompletion;
		this.initialVolatility = initialVolatility;
		this.owningPrincipal = setupOwningPrincipal( owningPrincipal );
		executionStateLock = new ReentrantReadWriteLock();
		executionStateReadLock = executionStateLock.readLock();
		executionStateWriteLock = executionStateLock.writeLock();
		persistentProcessStateMachine = new PersistentProcessStateMachine();
		childOIDs = new HashSet< PProcessOID >();
		pProcessExecutionSegmentStack = new Stack< PProcessExecutionSegment >();
		pProcessExecutionSegmentResult = new PProcessExecutionSegmentResult();
		requestSuspension = false;
		requestCancellation = false;
		systemLog = "";
		applicationLog = "";
		ownableObjectOIDs = new HashSet< POwnableObjectOID >();
		parentOID = setupChildParentRelationship();
		volatileResources = new HashSet< VolatileResource >();
		pProcessEventHistory = new ArrayList< PProcessEvent >();
		eventQueue = setupPEventQueue();
		eventTopic = setupPEventTopic();
	}
	
	private PProcessOID setupChildParentRelationship()
	{
		PProcessOID parentOID = getCurrentProcessOID();
		
		if ( parentOID != null )
		{
			PersistentProcess pProcess =  PersistentProcessImpl.getOtherProxy( parentOID );
			pProcess.addChildOID( getOID() );
		}
		
		return parentOID;
	}
	
	private PEventQueue setupPEventQueue()
	{
		// Normally, the owner and owning objects are setup automatically in PersistentObjectImpl;
		// however this is a special case, since we are in the constructor and the object is not
		// yet accessible through the object pool. Therefore---in this case only---we setup the
		// ownership association manually.
		PEventQueue eventQueue = PersistentEventQueueImpl.create( DEFAULT_PEVENT_QUEUE_NAME + " for " + getName(), true, null );
		PersistentEventQueue eventQueueInternal = PersistentEventQueueImpl.getOtherProxy( eventQueue.getOID() );
		eventQueueInternal.setOwningProcessOID( getOID() );
		addOwnableObjectOID( eventQueueInternal.getOID() );
		
		return eventQueue;
	}
	
	private PEventTopic setupPEventTopic()
	{
		PEventTopic eventTopic = PersistentEventTopicImpl.create( DEFAULT_PEVENT_TOPIC_NAME + " for " + getName(), true, null );
		PersistentEventTopic topicInternal = PersistentEventTopicImpl.getOtherProxy( eventTopic.getOID() );
		topicInternal.setOwningProcessOID( getOID() );
		addOwnableObjectOID( topicInternal.getOID() );
		
		return eventTopic;
	}
	
	private Principal setupOwningPrincipal( Principal owner )
	{
		Principal finalOwner = null;
		
		if ( owner == null )
			;	// TODO: setup default owner
		else
			finalOwner = owner;
		
		return finalOwner;
	}
	
	@Override
	public String getDefaultName()
	{
		return PersistentProcess.class.getSimpleName() + "-" + getOID();
	}

	///////////////////
	// INTERNAL METHODS
	///////////////////
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public ReadLock getExecutionStateReadLock()
	{
		return executionStateReadLock;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public WriteLock getExecutionStateWriteLock()
	{
		return executionStateWriteLock;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public Runnable getRunnable()
	{
		return runnable;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK, validProcessStates=NEW_STATE )
	public void setRunnable( Runnable runnable )
	{
		this.runnable = runnable;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public Callable< ? > getCallable()
	{
		return callable;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK, validProcessStates=NEW_STATE )
	public void setCallable( Callable< ? > callable )
	{
		this.callable = callable;
	}

	@Override
	public Stack< PProcessExecutionSegment > getpProcessExecutionSegmentStack()
	{
		return pProcessExecutionSegmentStack;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK )
	public PProcessExecutionSegmentResult getpProcessExecutionSegmentResult()
	{
		return pProcessExecutionSegmentResult;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Principal getOwningPrincipal()
	{
		return owningPrincipal;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Set< POwnableObjectOID > getOwnableObjectOIDs()
	{
		return ownableObjectOIDs;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void addOwnableObjectOID( POwnableObjectOID ownableObjectOID )
	{
		ownableObjectOIDs.add( ownableObjectOID );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void removeOwnableObjectOID( POwnableObjectOID ownableObjectOID )
	{
		ownableObjectOIDs.remove( ownableObjectOID );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public POwnableObject[] getOwnableObjects()
	{
		List< POwnableObject > ownableObjects = PersistentOwnableObjectImpl.findByOwningProcess( getOID() );

		return ownableObjects.toArray( new POwnableObject[ ownableObjects.size() ] );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=NEW_STATE )
	public void setOwningPrincipal( Principal owningPrincipal )
	{
		this.owningPrincipal = owningPrincipal;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PProcessOID getParentOID()
	{
		return parentOID;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean getAutoCheckpointing()
	{
		return autoCheckpointing;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setAutoCheckpointing( boolean autoCheckpointing )
	{
		this.autoCheckpointing = autoCheckpointing;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean getDestroyOnCompletion()
	{
		return destroyOnCompletion;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setDestroyOnCompletion( boolean destroyOnCompletion )
	{
		this.destroyOnCompletion = destroyOnCompletion;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Set< PProcessOID > getChildOIDs()
	{
		return childOIDs;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=RUNNING_STATE )
	public void addChildOID( PProcessOID childOID )
	{
		childOIDs.add( childOID );
	}
	
	public static Logger getSystemLogger( String name )
	{
		return getSystemLogger( name, false );
	}
	
	public static Logger getSystemLogger( String name, boolean useParentHandlers )
	{
		return getPersistentLogger( "__systemLogger." + name, PSystemLogHandler.class, useParentHandlers );
	}
	
	public static Logger getApplicationLogger( String name )
	{
		return getApplicationLogger( name, false );
	}
	
	public static Logger getApplicationLogger( String name, boolean useParentHandlers )
	{
		return getPersistentLogger( "__applicationLogger." + name, PApplicationLogHandler.class, useParentHandlers );
	}

	private static Logger getPersistentLogger( String name, Class< ? > handlerClass, boolean useParentHandlers )
	{
		// Note that logger has no handlers, but it is configured by default
		// to send its output to its parent logger.
		
		Logger logger = Logger.getLogger( name );
		logger.setUseParentHandlers( useParentHandlers );
		Handler targetHandler = null;

		// Make sure that each logger has exactly one persistent handler
		// (BTW: this is consistent with the logging architecture, since 
		// handlers generally have a one to one relationship with the
		// log file; or in our case, the class fields).
		for ( Handler handler : logger.getHandlers() )
		{
			if ( handlerClass.isAssignableFrom( handler.getClass() ) )
			{
				targetHandler = handler;
				break;
			}
		}
		
		if ( targetHandler == null )
		{
			targetHandler = (Handler)ClassWrapper.newInstance( handlerClass );
			targetHandler.setFormatter( new SimpleFormatter() );
			logger.addHandler( targetHandler );
		}
		
		return logger;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public String getSystemLog()
	{
		return systemLog;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public void setSystemLog( String systemLog )
	{
		this.systemLog = systemLog;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public String getApplicationLog()
	{
		return applicationLog;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public void setApplicationLog( String applicationLog )
	{
		this.applicationLog = applicationLog;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public StackTraceElement[] getStackTrace()
	{
		List< StackTraceElement > stackTrace = new ArrayList< StackTraceElement >();
		
		for ( int i = pProcessExecutionSegmentStack.size() - 1 ; i >= 0 ; --i )
		{
			PProcessExecutionSegment pProcessExecutionSegment = pProcessExecutionSegmentStack.get( i );
			
			PFrameStack pFrameStack = pProcessExecutionSegment.getPFrameStack();
			
			for ( int j = pFrameStack.size() - 1 ; j >= 0 ; --j )
				stackTrace.add( pFrameStack.get( j ).getStackTraceElement() );
		}
		
		return stackTrace.toArray( new StackTraceElement[ stackTrace.size() ] );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK, executionStateProtection=EXECUTION_READ_LOCK )
	public void setCheckpoint()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void fireEvent( String event )
	{
		persistentProcessStateMachine.fireEvent( event );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean isInState( String state )
	{
		return persistentProcessStateMachine.isInState( state );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean getRequestedSuspension()
	{
		return requestSuspension;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setRequestSuspension( boolean requestSuspension )
	{
		this.requestSuspension = requestSuspension;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean getRequestCancellation()
	{
		return requestCancellation;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void setRequestCancellation( boolean requestCancellation )
	{
		this.requestCancellation = requestCancellation;
	}
	
	public static PProcessOID getCurrentProcessOID()
	{
		return currentProcessOID.get();
	}
	
	public static void setCurrentProcessOID( PProcessOID persistentProcessOID )
	{
		currentProcessOID.set( persistentProcessOID );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public PProcessPMethodCommunicationArea getPProcessPMethodCommunicationArea()
	{
		return getpProcessExecutionSegmentStack().peek().getPProcessPMethodCommunicationArea();
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void notifyActivate()
	{
		signalEvent( PersistentProcessStateMachine.ACTIVATE_EVENT );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void notifyPassivate()
	{
		signalEvent( PersistentProcessStateMachine.PASSIVATE_EVENT );
	}

	// TODO figure out a way of invoking this method from the method pool
	// when the process is destroyed. The challenge lies in not causing a
	// race condition between the object pool's destruction of the (composite)
	// event topic and the signaling of the process to that topic.
	//
	// Idea: what if the destruction process worked as follows:
	// 1. Invoke notifyDestroy() on process.
	// 2. Invoke notifyDestroy() on composite parts.
	// 3. Destroy composite parts.
	// 4. Destroy process.
	// 
	// Note: I think passivation/activation should follow the same guidelines,
	// since you otherwise run the risk of, e.g., awakening a topic that was
	// just put to sleep merely to signal the process's passivation.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void notifyDestroy()
	{
		signalEvent( PersistentProcessStateMachine.DESTROY_EVENT );
	}
	
	/////////////////////
	// VOLATILITY METHODS
	/////////////////////
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=RUNNING_STATE )
	public void notifyVolatilityChange( VolatilityChangeType volatilityChangeType )
	{
		Volatility oldVolatility = getSelfProxy().getVolatility();
		Volatility newVolatility = recalcVolatility();
		
		if ( ( volatilityChangeType.equals( VolatilityChangeType.PUSH_FRAME ) || volatilityChangeType.equals( VolatilityChangeType.ENLIST_RESOURCE ) ) &&
			 oldVolatility.equals( Volatility.VOLATILE ) && newVolatility.equals( Volatility.STABLE ) )
			throw new VolatilityException( "Illegal change to process volatility: cannot go from volatile to stable." );
		
		if ( oldVolatility.equals( Volatility.STABLE ) && newVolatility.equals( Volatility.VOLATILE ) )
		{
			signalEvent( PersistentProcessStateMachine.DESTABILIZE_EVENT );
		}
		else if ( oldVolatility.equals( Volatility.VOLATILE ) && newVolatility.equals( Volatility.STABLE ) )
		{
			signalEvent( PersistentProcessStateMachine.STABILIZE_EVENT );
		}
	}

	private Volatility recalcVolatility()
	{
		PFrame currentFrame = getCurrentPFrame();
		Volatility volatility = null;
		
		if ( currentFrame == null )
			volatility = initialVolatilityActual;
		else
			volatility = currentFrame.getVolatility();
		
		if ( volatility.equals( Volatility.STABLE ) )
		{
			volatility = getSelfProxy().getVolatileResourcesVolatility();
		}
		
		return volatility;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Volatility getInitialVolatility()
	{
		return initialVolatility;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=NEW_STATE )
	public void setInitialVolatility( Volatility initialVolatility )
	{
		this.initialVolatility = initialVolatility;
	}
	
	// Note that VolatileResource, VolatileResourceImpl, etc. will need to
	// be migrated to implement this: they need to hold a reference to VolatileObject
	// to invoke the notifyVolatilityChange() method.
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=RUNNING_STATE )
	@Override
	public VolatileResource createVolatileResource()
	{
		// TODO implement this method.
		return null;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, validProcessStates=RUNNING_STATE )
	public VolatileResource destroyVolatileResource()
	{
		// TODO implement this method.
		return null;
	}
	
	// Note that this method is protected by STATE_READ_LOCK because we need to
	// ensure that the state machine doesn't change while the branches (if/else)
	// are being executed. Although going through the proxy (getSelfProxy())is
	// redundant in terms of the lock involved it is necessary to ensure other
	// constraints (e.g., validProcessStates, etc.) are enforced.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Volatility getVolatility()
	{
		Volatility volatility = null;
		
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.VOLATILE_STATE ) )
			volatility = Volatility.VOLATILE;
		else if ( getSelfProxy().isInState( PersistentProcessStateMachine.STABLE_STATE ) )
			volatility = Volatility.STABLE;
		else
			throw new IllegalStateException( "Unexpected state of state machine." );
		
		return volatility;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK, validProcessStates=RUNNING_STATE )
	public Volatility getVolatileResourcesVolatility()
	{
		Volatility volatility = Volatility.STABLE;

		for ( VolatileResource volatileResource : volatileResources )
		{
			if ( volatileResource.isEnlisted() )
			{
				volatility = Volatility.VOLATILE;
				break;
			}
		}
		
		return volatility;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Event tryListen()
	{
		return eventQueue.tryListen();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Event tryListen( String filterExpression )
	{
		return eventQueue.tryListen( filterExpression );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public void send( Event event )
	{
		eventQueue.send( event );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription )
	{
		return eventTopic.subscribe( targetQueueOID, filterExpr, singleEventSubscription );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public boolean unsubscribe( UUID subscriptionOID )
	{
		return eventTopic.unsubscribe( subscriptionOID );
	}
	
	///////////////////
	// CONTROL METHODS
	///////////////////
	
	// Note that this method delegates to the (async) startProcess() method
	// which is protected by EXECUTION_WRITE_LOCK.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, validProcessStates=NEW_STATE )
	public void start()
	{
		// Determine invokingProcessVolatility.
		Volatility currentVolatility = null;
		PersistentProcess currentProcess = getSelfProxyCurrentProcess();
		
		if ( currentProcess != null )
			currentVolatility = currentProcess.getVolatility();

		signalEvent( PersistentProcessStateMachine.START_EVENT );
		
		// Start the process async.
		getSelfProxy().startProcess( currentVolatility );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void suspend()
	{
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.RUNNING_STATE ) )
			getSelfProxy().setRequestSuspension( true );
		else if ( getSelfProxy().isInState( PersistentProcessStateMachine.WAITING_STATE ) )
			signalEvent( PersistentProcessStateMachine.SUSPEND_EVENT );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void resume()
	{
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.SUSPENDED_STATE ) )
		{
			signalEvent( PersistentProcessStateMachine.RESUME_EVENT );
			getSelfProxy().resumeProcess();
		}
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void cancel()
	{
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.RUNNING_STATE ) )
			getSelfProxy().setRequestCancellation( true );
		else if ( getSelfProxy().isInState( PersistentProcessStateMachine.PENDING_STATE ) )
			getSelfProxy().terminateProcess( null );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public Class< ? > getRunnableType()
	{
		Class< ? > runnableType = null;
		
		if ( runnable != null )
			runnableType = runnable.getClass();
		
		return runnableType;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
 	public Class< ? > getCallableType()
	{
		Class< ? > callableType = null;
		
		if ( callable != null )
			callableType = callable.getClass();
		
		return callableType;
	}
	
	//////////////////////////////////////
	// SPECIAL METHODS (HANDLED BY ENGINE)
	//////////////////////////////////////

	@Override
	public void sleep( long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public void sleep( Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public void sleep( Volatility volatility, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public void sleep( Volatility volatility, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join() { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join( long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join( Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join( Volatility volatility ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join( Volatility volatility, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public void join( Volatility volatility, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen() { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( String filterExpression ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( String filterExpression, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( String filterExpression, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility, String filterExpression ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility, String filterExpression, long time, TimeUnit timeUnit ) { throw new UnsupportedOperationException(); }

	@Override
	public Event listen( Volatility volatility, String filterExpression, Date deadline ) { throw new UnsupportedOperationException(); }

	@Override
	public < T > Future< T > getFuture() { throw new UnsupportedOperationException(); }
	
	////////////////////
	// EXECUTION METHODS
	////////////////////
	
	@Override
	@ProxyMethod( synchronicity=ASYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK, validProcessStates=RUNNING_STATE )
	public void startProcess( Volatility currentVolatility )
	{
		try
		{
			prepareProcessToStart( currentVolatility );
			executeProcess();
		}
		catch ( Throwable t )
		{
			// Note that any exception making it this far is by definition
			// an internal error and must cause the process to terminate.
			terminateProcess( t );
		}
	}

	@Override
	@ProxyMethod( synchronicity=ASYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK )
	public void handleEvent( Event event )
	{
		try
		{
			handleEventWithExceptionHandling( event );
		}
		catch ( Throwable t )
		{
			// See startProcess().
			terminateProcess( t );
		}
	}
	
	private void handleEventWithExceptionHandling( Event event )
	{
		PersistentEventQueue eventQueue = PersistentEventQueueImpl.getOtherProxy( event.getPEventQueueOID() );

		if ( eventQueue.unregisterListener() )
			handleEventWithoutUnregisteringListener( event );
	}
	
	private void handleEventWithoutUnregisteringListener( Event event )
	{
		getSelfProxy().handleEventPrepareProcessToContinue( event );
		
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.RUNNING_STATE ) )
			executeProcess();
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK, stateProtection=STATE_WRITE_LOCK )
	public void handleEventPrepareProcessToContinue( Event event )
	{
		Object retVal = null;
		Throwable exception = null;
		
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.WAITING_STATE ) )
		{
			if ( getSelfProxy().isInState( PersistentProcessStateMachine.LISTENING_STATE ) )
			{
				if ( !(event instanceof TimeoutEvent) )
					retVal = event;
				signalEvent( PersistentProcessStateMachine.EVENT_RECEIVED_EVENT );
			}
			else if ( getSelfProxy().isInState( PersistentProcessStateMachine.JOINING_STATE ) )
			{
				signalEvent( PersistentProcessStateMachine.PROCESS_JOINED_EVENT );
			}
			else if ( getSelfProxy().isInState( PersistentProcessStateMachine.SLEEPING_STATE ) )
			{
				signalEvent( PersistentProcessStateMachine.SLEEP_COMPLETED_EVENT );
			}

			prepareProcessToContinue( retVal, exception );
		}
		else if ( getSelfProxy().isInState( PersistentProcessStateMachine.SUSPENDED_STATE ) )
		{
			eventReceivedDuringSuspension = event;
		}
	}

	@Override
	@ProxyMethod( synchronicity=ASYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK )
	public void resumeProcess()
	{
		try
		{
			resumeProcessWithExceptionHandling();
		}
		catch ( Throwable t )
		{
			// See startProcess().
			terminateProcess( t );
		}
	}
	
	private void resumeProcessWithExceptionHandling()
	{
		if ( eventReceivedDuringSuspension != null )
		{
			handleEventWithoutUnregisteringListener( eventReceivedDuringSuspension );
			eventReceivedDuringSuspension = null;
		}
		else
		{
			executeProcess();
		}
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK )
	public PProcessExecutionSegmentResult invokeStaticIntializer( Class< ? > theClass ) throws ClassNotFoundException
	{
		PProcessExecutionSegmentResult result = null;
		
		try
		{
			result = invokeStaticIntializerWithExceptionHandling( theClass );
		}
		catch ( Throwable t )
		{
			throw new ClassNotFoundException( "Unable to load class due to internal error.", t );
		}
		
		return result;
	}

	private PProcessExecutionSegmentResult invokeStaticIntializerWithExceptionHandling( Class< ? > theClass )
	{
		PProcessExecutionSegmentResult result = null;
		
		pushPProcessExecutionSegment();

		prepareProcessToInvokeStaticInitializer( theClass );
		executeProcess();
		result = getpProcessExecutionSegmentResult();
		
		return result;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK, executionStateProtection=EXECUTION_WRITE_LOCK, validProcessStates=PENDING_STATE )
	public void terminateProcess( Throwable exception )
	{
		// Reset the call stack.
		pProcessExecutionSegmentStack.clear();
		
		// Set the process result.
		if ( exception != null )
			pProcessExecutionSegmentResult.setException( new SaraswatiInternalError( exception ) );

		// Bring the state machine to the TERMINATED state and
		// notify listeners that we terminated.
		persistentProcessStateMachine.resetMachine();
		persistentProcessStateMachine.fireEvent( PersistentProcessStateMachine.START_EVENT );
		signalEvent( PersistentProcessStateMachine.CANCEL_EVENT );
	}
	
	// For testing purposes only.
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_WRITE_LOCK )
	public void invokeTestMethod( Volatility currentVolatility, String testClassName, String methodName, Class< ? >[] paramTypes, Object[] params )
	{
		prepareProcessToInvokeTestMethod( currentVolatility, testClassName, methodName, paramTypes, params );
		executeProcess();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public PProcessHandle getPProcessHandle()
	{
		return createPProcessHandleProxy( getSelfProxy().getOID() );
	}
	
	private void prepareProcessToStart( Volatility currentVolatility )
	{
		prepareProcess( currentVolatility );
		
		FQJVMMethodName initialMethodName = null;
		Object callableOrRunnable = null;

		if ( runnable != null )
		{
			callableOrRunnable = runnable;
			initialMethodName = new FQJVMMethodName( runnable.getClass(), RUN_METHOD );
		}
		else if ( callable != null )
		{
			callableOrRunnable = callable;
			
			// Note that this is a bit of a workaround for the fact that Callable.call() returns
			// a generic type and I haven't really built in support for generics yet. So to make
			// this work without changing FQJVMMethodName (and without having to think about it
			// too much at this point) I'm just looking up the actual return type and using
			// that (rather than java.lang.Object), which corresponds to the entry in the
			// virtual invocation table.
			Method method = ClassWrapper.getMethod( callable.getClass(), FieldAndMethodConstants.CALL_METHOD, new Class[] {} );
			Class< ? > returnClass = (Class< ? >)method.getGenericReturnType();
			initialMethodName = new FQJVMMethodName( callable.getClass(), "call", returnClass );
		}
		else
		{
			throw new RuntimeException( "Neither a runnable nor a callable object was specified." );
		}
		
		MethodInvocationInstance miInstance = new MethodInvocationInstance( MethodInvocationType.INVOKE_VIRTUAL, initialMethodName, callableOrRunnable, new Object[] {}, false );
		setNextMethodInvocationInstance( miInstance );
	}
	
	private void prepareProcessToInvokeTestMethod( Volatility currentVolatility, String testClassName, String methodName, Class< ? >[] paramTypes, Object[] params )
	{
		signalEvent( PersistentProcessStateMachine.START_EVENT );
		prepareProcess( currentVolatility );
		
		// Get the class and method.
		Class< ? > theClass = JavaClassHelper.getClass( testClassName );
		Method theMethod = ClassWrapper.getDeclaredMethod( theClass, methodName, paramTypes );
		Object targetObject = null;
		MethodInvocationType miType = null;
		
		// Setup miType and targetObject depending on whether method is static or not.
		if ( Modifier.isStatic( theMethod.getModifiers() ) )
		{
			miType = MethodInvocationType.INVOKE_STATIC;
		}
		else
		{
			targetObject = ClassWrapper.newInstance( theClass );
			miType = MethodInvocationType.INVOKE_VIRTUAL;
		}
		
		// Setup the initial invocation instance.
		FQJVMMethodName fqMethodName = new FQJVMMethodName( theClass, theMethod );
		MethodInvocationInstance miInstance = new MethodInvocationInstance( miType, fqMethodName, targetObject, params, false );
		setNextMethodInvocationInstance( miInstance );
	}
	
	private void prepareProcess( Volatility currentVolatility )
	{
		// Setup initialVolatility.
		getSelfProxy().setupInitialVolatilityActual( currentVolatility );

		// Notify of volatility change.
		getSelfProxy().notifyVolatilityChange( VolatilityChangeType.INITIAL );
		
		// Setup the initial execution segment
		pushPProcessExecutionSegment();
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public void setupInitialVolatilityActual( Volatility currentVolatility )
	{
		if ( currentVolatility == null )
		{
			if ( initialVolatility == null )
				initialVolatilityActual = Volatility.STABLE;
			else
				initialVolatilityActual = initialVolatility;
		}
		else
		{
			initialVolatilityActual = currentVolatility;
		}
	}
	
	private void prepareProcessToContinue( Object retVal, Throwable exception )
	{
		setMethodResultInstance( retVal, exception );
	}
	
	private void prepareProcessToInvokeStaticInitializer( Class< ? > theClass )
	{
		FQJVMMethodName fqMethodName = new FQJVMMethodName( theClass, FieldAndMethodConstants.CLINIT_METHOD, void.class );
		MethodInvocationInstance miInstance = new MethodInvocationInstance( MethodInvocationType.INVOKE_STATIC, fqMethodName, this, new Object[] {}, false );
		setNextMethodInvocationInstance( miInstance );
	}
	
	private void executeProcess()
	{
		while ( getSelfProxy().isInState( PersistentProcessStateMachine.CURRENT_EXECUTION_SEGMENT_PENDING_STATE ) )
			executeNextTick();
		
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE ) )
		{
			if ( getpProcessExecutionSegmentStack().size() > 0 )
			{
				signalEvent( PersistentProcessStateMachine.CONTINUE_WITH_CURRENT_SEGMENT_EVENT );
			}
			else
			{
				ConcurrentTestingHelper.await( 
					ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_TEST_NAME, 
					ConcurrentTestingHelper.JOIN_SUBSCRIBE_BEFORE_PUBLISH_SUBSCRIBED_CONDITION );

				PProcessExecutionSegmentResult result = getpProcessExecutionSegmentResult();
				String event = PersistentProcessStateMachine.PROCESS_COMPLETED_EVENT;
				
				if ( result.getException() != null )
					event = PersistentProcessStateMachine.PROCESS_THREW_EXCEPTION_EVENT;

				signalEvent( event );

				ConcurrentTestingHelper.signal( 
					ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_TEST_NAME, 
					ConcurrentTestingHelper.JOIN_PUBLISH_BEFORE_SUBSCRIBE_PUBLISHED_CONDITION );
			}
		}
	}
	
	// TODO I think we should consider doing some refactoring in the area of state
	// machine/PProcessEvent. Some things to consider are:
	// 1. Factor active/passive into common base type, i.e., PersistentObjectStateMachine.
	//    Perhaps also factor internal states (CURRENT_EXECUTION_SEGMENT_PENDING_STATE, 
	//    CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE) into separate (internal) state machine
	//    (would simplify distinction between internal/external states).
	// 2. Move state history into state machine and allow for historizing only certain states.
	// 3. Introduce state machine listener hook --> list of PEvents to send are generated through
	//    the hook.
	// 4. Allows us to get rid of createDate in PersistentObjectImpl.
	private void signalEvent( String event )
	{
		String[] statesBefore = getSelfProxy().getStates();
		getSelfProxy().fireEvent( event );
		String[] statesAfter = getSelfProxy().getStates();
		
		List< PProcessEvent > pProcessEvents = PProcessEvent.determineEvents( statesBefore, statesAfter, event );
		pProcessEventHistory.addAll( pProcessEvents );

		for ( PProcessEvent pProcessEvent : pProcessEvents )
		{
			eventTopic.publish( pProcessEvent );
			logPProcessEvent( pProcessEvent );
		}
	}
	
	private void logPProcessEvent( PProcessEvent pProcessEvent )
	{
		String msg = null;
		
		String eventOrState = pProcessEvent.getProperty( PProcessEvent.ENTERED_PROCESS_STATE );
		
		if ( eventOrState != null )
			msg = "Entered state: " + eventOrState;
		
		if ( msg == null )
		{
			eventOrState = pProcessEvent.getProperty( PProcessEvent.EXITED_PROCESS_STATE );

			if ( eventOrState != null )
				msg = "Exited state: " + eventOrState;
		}
		
		if ( msg == null )
		{
			eventOrState = pProcessEvent.getProperty( PProcessEvent.EVENT_OCCURED );

			if ( eventOrState != null )
				msg = "Event occured: " + eventOrState;
		}

		if ( msg == null )
			throw new IllegalStateException( "No event or state found for pProcessEvent: " + pProcessEvent );
		
		logToSystemLog( Level.INFO, msg );
	}
	
	private void logToSystemLog( Level level, String msg )
	{
		// To ensure that all events are logged to this process, we set the
		// current process to this process before using the logger. Certain methods
		// (i.e., start()) do not execute in the context of this process, but rather
		// some external process, which may be null (if invoked from a thread).
		// TODO: This is a bit of a work-around, and we should probably find a more
		// elegant solution at some point.
		PProcessOID currentOID = getCurrentProcessOID();
		
		try
		{
			setCurrentProcessOID( getOID() );
			getSystemLogger( PEngine.class.getName() ).log( level, msg );
		}
		finally
		{
			setCurrentProcessOID( currentOID );
		}
	}
	
	// Three cases of exceptions:
	// 1. InvocationTargetExceptionWrapper (recoverable): an exception was thrown
	//    by an invoked method:
	//    a) If the invoked method was persistent --> pop the last frame off of the
	//       stack.
	//    b) If the invoked method was non-persistent --> do not pop the last frame
	//       off of the stack.
	//    In any case, adjust the operand stack to reflect the exception and set
	//    the next method invocation to the persistent method that triggered the
	//    invocation.
	// 2. VolatilityExceptoin (recoverable): an exception was thrown due to an
	//    illegal change in process volatility. Handle this case analogous to 1.a. above.
	// 3. Any other exception (non-recoverable): in this case, the process is in
	//    an unknown state; e.g., the call-stack might or might-not have had a frame
	//    pushed onto it, the operand stack might or might not be adjusted, or might be
	//    partially adjusted, etc. The only option here is to throw a SaraswatiInternalError
	//    which causes the entire process to abort. The call-stack is cleared and the
	//    SaraswatiInternalError is set as the final result.
	private void executeNextTick()
	{
		Object returnValue = null;
		Throwable exception = null;

		try
		{
			setNextMethodInvocationInstance();

			if ( isSpecialMethod() )
				returnValue = invokeSpecialMethod();
			else
				returnValue = invokeNextMethodWoExceptionHandling();
		}
		// Handle expected/recoverable exceptions. Anything not caught here
		// is unexpected/non-recoverable and will be cause the process to
		// immediately terminate (see PProcessContextInvocationHandler).
		catch ( ProcessExecutionException e )
		{
			exception = e.getCause();
		}
		catch ( VolatilityException e )
		{
			exception = e;
		}
		
		if ( exception != null )
			adjustStackTrace( exception );
		
		// Only set a method result if the process is still in the running state.
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.RUNNING_STATE ) )
			setMethodResultInstance( returnValue, exception );
		
		// Check for suspension or cancellation requests.
		getSelfProxy().checkForSuspensionOrCancellation();
	}
	
	private void adjustStackTrace( Throwable exception )
	{
		StackTraceElement[] threadTrace = exception.getStackTrace();

		// Find the top-most frame in the stack trace related to an engine class.
		int topMostEngineFrame = -1;
		
		for ( int i = 0 ; i < threadTrace.length ; ++i )
		{
			if ( threadTrace[ i ].getClassName().equals( PersistentProcessImpl.class.getName() ) )
			{
				topMostEngineFrame = i;
				break;
			}
		}
		
		// If no engine frame was located in the stack trace --> this trace requires no adjustment.
		if ( topMostEngineFrame == -1 )
			return;

		// Go back up the stack until we hit a non-reflection frame --> this is the
		// beginning of the stack trace we want to extract.
		int lastReflectionFrame = 0;
		
		for ( int i = topMostEngineFrame - 1 ; i >= 0 ; --i )
		{
			String className = threadTrace[ i ].getClassName();
			
			if ( !( className.startsWith( "java.lang.reflect" ) || className.startsWith( "sun.reflect" ) ) )
			{
				lastReflectionFrame = i + 1;
				break;
			}
		}
		
		StackTraceElement[] persistentTrace = getSelfProxy().getStackTrace();
		StackTraceElement[] newTrace = new StackTraceElement[ persistentTrace.length + lastReflectionFrame ];

		for ( int i = 0 ; i < lastReflectionFrame ; ++i )
			newTrace[ i ] = threadTrace[ i ];
		
		for ( int i = 0 ; i < persistentTrace.length ; ++i )
			newTrace[ i + lastReflectionFrame ] = persistentTrace[ i ];
		
		exception.setStackTrace( newTrace );
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_WRITE_LOCK )
	public void checkForSuspensionOrCancellation()
	{
		// Check for suspension or cancellation requests.
		if ( getSelfProxy().getVolatility().equals( Volatility.STABLE ) )
		{
			if ( getSelfProxy().getRequestedSuspension() )
			{
				getSelfProxy().setRequestSuspension( false );
				signalEvent( PersistentProcessStateMachine.SUSPEND_EVENT );
			}
			else if ( getSelfProxy().getRequestCancellation() )
			{
				getSelfProxy().setRequestCancellation( false );
				getSelfProxy().terminateProcess( null );
			}
		}
	}

	private boolean isSpecialMethod()
	{
		FQJVMMethodName fqMethodName = getNextMethodInvocationInstance().getFQMethodName();
		boolean isSpecialMethod = SPECIAL_METHODS.contains( fqMethodName );
		
		return isSpecialMethod;
	}
	
	private Object invokeSpecialMethod()
	{
		Object retVal = null;
		FQJVMMethodName fqMethodName = getNextMethodInvocationInstance().getFQMethodName();

		if ( LISTEN_METHODS.contains( fqMethodName ) )
			handleListen();
		else if ( JOIN_METHODS.contains( fqMethodName ) )
			handleJoin();
		else if ( SLEEP_METHODS.contains( fqMethodName ) )
			handleSleep();
		else
			throw new IllegalStateException( "Unexpected value for special method: " + fqMethodName );

		// Setting the values for invokedMethodName to null signals to
		// setMethodResultInstance() that no nested method was invoked.
		FQJVMMethodName invokedMethodName = getPProcessPMethodCommunicationArea().getInvokedMethodName();
		invokedMethodName.setJVMClassName( null );
		invokedMethodName.setJVMMethodName( null );
		invokedMethodName.setJVMMethodDescriptor( null );
		
		return retVal;
	}
	
	private void handleListen()
	{
		// Transition to the LISTENING state.
		signalEvent( PersistentProcessStateMachine.LISTEN_EVENT );
		
		// Get the handle of the target event queue
		Object handleAsObject = getNextMethodInvocationInstance().getObjectInstance();
		
		if ( handleAsObject == null )
			throw new ProcessExecutionException( new NullPointerException() );

		if ( handleAsObject instanceof PEventQueue )
		{
			PEventQueue pEventQueue = (PEventQueue)handleAsObject;
			PersistentEventQueue persistentEventQueue = PersistentEventQueueImpl.getOtherProxy( pEventQueue.getOID() );
			persistentEventQueue.registerListener( getOID(), getFilterExpression(), getTimeSpecification() );
		}
		else if ( handleAsObject instanceof PProcess )
		{
			PProcess pProcess = (PProcess)handleAsObject;
			PersistentProcess persistentProcess = PersistentProcessImpl.getOtherProxy( pProcess.getOID() );
			PersistentEventQueue persistentEventQueue = PersistentEventQueueImpl.getOtherProxy( persistentProcess.getEventQueueOID() );
			persistentEventQueue.registerListener( getOID(), getFilterExpression(), getTimeSpecification() );
		}
		else
			throw new IllegalStateException( "Unexpectd type for handleAsObject: " + handleAsObject.getClass().getName() );
	}
	
	private void handleJoin()
	{
		// Transition to the JOINING state.
		signalEvent( PersistentProcessStateMachine.JOIN_EVENT );
		
		// Get the handle of the target process.
		Object handleAsObject = getNextMethodInvocationInstance().getObjectInstance();
		
		if ( handleAsObject == null )
			throw new ProcessExecutionException( new NullPointerException() );
		
		PProcessHandle pProcessHandle = (PProcessHandle)handleAsObject;
		PersistentProcess pProcess = PersistentProcessImpl.getOtherProxy( pProcessHandle.getOID() );
		
		// Subscribe this event's queue to the target process's topic.
		String topicfilter = "event.getProperty( '" + PProcessEvent.ENTERED_PROCESS_STATE + "' ).equals( '" + PProcessEvent.TERMINATED_STATE +
				"' ) && event.getSenderOID().toString().equals( '" + pProcess.getOID().toString() + "' )";
		UUID subscriptionOID = pProcess.subscribe( eventQueue.getOID(), topicfilter, true );
		
		// Register as a listener of this event's queue.
		PersistentEventQueue pEventQueue = PersistentEventQueueImpl.getOtherProxy( eventQueue.getOID() );
		String queuefilter = "event.getSubscriptionOID().toString().equals( '" + subscriptionOID + "' )";
		pEventQueue.registerListener( getSelfProxy().getOID(), queuefilter, getTimeSpecification() );
		
		// Decide whether to park the process or not.
		boolean parkProcess = false;
		
		// If the process was in the TERMINATED_STATE after registering to recieve
		// PROCESS_TERMINATED_EVENT (above); depending on timing, we may or may not
		// actually receive the event.
		if ( pProcess.isInState( PersistentProcessStateMachine.TERMINATED_STATE ) )
		{
			// Unsubscribe from topic and unregister as listener with queue.
			pProcess.unsubscribe( subscriptionOID );
			boolean success = pEventQueue.unregisterListener();
			
			// success == true indicates that the event is already in the process of being
			// delivered (the delivering thread will wait for this thread to release it's
			// lock on the process). In this case, we have no choice but to park the thread
			// and allow the delivering thread to complete the join.
			if ( !success )
				parkProcess = true;
		}
		// If the process was not in the TERMINATED_STATE after registering to receive
		// PROCESS_TERMINATED_EVENT --> we know for sure that we will receive the event
		// and can thus park this process.
		else
		{
			parkProcess = true;
		}
			
		if ( !parkProcess )
			signalEvent( PersistentProcessStateMachine.PROCESS_JOINED_EVENT );
	}
	
	private void handleSleep()
	{
		// Transition to the SLEEPING state.
		signalEvent( PersistentProcessStateMachine.SLEEP_EVENT );
		
		// Register as a listener of this event's queue. Note that the filter
		// expression is guaranteed to evaluate to false forcing the timeout
		// to occur, which is what we want.
		PersistentEventQueue pEventQueue = PersistentEventQueueImpl.getOtherProxy( eventQueue.getOID() );
		pEventQueue.registerListener( getSelfProxy().getOID(), "false", getTimeSpecification() );
	}
	
	private String getFilterExpression()
	{
		FQJVMMethodName fqMethodName = getNextMethodInvocationInstance().getFQMethodName();
		Object[] params = getNextMethodInvocationInstance().getParameters();
		String filterExpression = null;
		
		if ( FILTER_SIGNATURE_METHODS.contains( fqMethodName ) )
		{
			int paramOffset = 0;
			
			if ( FILTER_SIGNATURE_METHODS1.contains( fqMethodName ) )
				paramOffset = 0;
			else if ( FILTER_SIGNATURE_METHODS2.contains( fqMethodName ) )
				paramOffset = 1;
			else
				throw new IllegalStateException( "fqMethodName not in expected set." );
			
			filterExpression = (String)params[ paramOffset ];
		}
		
		return filterExpression;
	}
	
	private TimeSpecification getTimeSpecification()
	{
		FQJVMMethodName fqMethodName = getNextMethodInvocationInstance().getFQMethodName();
		Object[] params = getNextMethodInvocationInstance().getParameters();
		TimeSpecification timeSpecification = null;
		
		if ( TIME_AND_TIMEUNIT_SIGNATURE_METHODS.contains( fqMethodName ) )
		{
			int paramOffset = 0;
			
			if ( TIME_AND_TIMEUNIT_SIGNATURE_METHODS1.contains( fqMethodName ) )
				paramOffset = 0;
			else if ( TIME_AND_TIMEUNIT_SIGNATURE_METHODS2.contains( fqMethodName ) )
				paramOffset = 1;
			else if ( TIME_AND_TIMEUNIT_SIGNATURE_METHODS3.contains( fqMethodName ) )
				paramOffset = 2;
			else
				throw new IllegalStateException( "fqMethodName not in expected set." );
			
			long time = (long)params[ paramOffset ];
			TimeUnit timeUnit = (TimeUnit)params[ paramOffset + 1 ];
			timeSpecification = new TimeAndUnitSpecification( time, timeUnit );
		}
		else if ( DEADLINE_SIGNATURE_METHODS.contains( fqMethodName ) )
		{
			int paramOffset = 0;
			
			if ( DEADLINE_SIGNATURE_METHODS1.contains( fqMethodName ) )
				paramOffset = 0;
			else if ( DEADLINE_SIGNATURE_METHODS2.contains( fqMethodName ) )
				paramOffset = 1;
			else if ( DEADLINE_SIGNATURE_METHODS3.contains( fqMethodName ) )
				paramOffset = 2;
			else
				throw new IllegalStateException( "fqMethodName not in expected set." );

			Date deadline = (Date)params[ paramOffset ];
			timeSpecification = new DeadlineSpecification( deadline );
		}
		
		return timeSpecification;
	}
	
	// TODO: if 
	private Object invokeNextMethodWoExceptionHandling()
	{
		getPProcessPMethodCommunicationArea().setIsContainerInvocation( true );

		MethodInvocationInstance nextMiInstance = getNextMethodInvocationInstance();
		FQJVMMethodName resolvedFqMethodName = nextMiInstance.getResolvedFqMethodName();
		
		// TODO: catch any ClassNotFoundExceptions that are thrown on behalf of
		// the user code and wrap them in ProcessExecutionExceptions.
		boolean isPersistentClass = nextMiInstance.isResolvedClassPersistent();
		boolean requiresObjectConstruction = nextMiInstance.getRequiresObjectConstruction();
		Class< ? > theClass = JavaClassHelper.getClassWithJVMName( resolvedFqMethodName.getJVMClassName() );
		Class< ? >[] paramTypes = JavaClassHelper.getParamTypes( resolvedFqMethodName.getJVMMethodDescriptor() );
		String methodName = resolvedFqMethodName.getJVMMethodName();
		Object targetObject = nextMiInstance.getObjectInstance();
		Object[] params = nextMiInstance.getParameters();
		Object methodRetVal = null;
		boolean invokeMethod = true;
		
		// Ignore invocations of the java.lang.Object constructor
		// (these only occur in the __saraswati_init method of a
		// class that was loaded through PClassLoader and
		//  inherits from java.lang.Object).
		if ( theClass.equals( Object.class ) && nextMiInstance.getFQMethodName().getJVMMethodName().equals( FieldAndMethodConstants.INIT_METHOD ) )
			invokeMethod = false;
		
		// Was this an instance of the new operator? If so --> create object instance.
		if ( requiresObjectConstruction )
		{
			if ( isPersistentClass )
			{
				// Since the initializer of a persistent class may be invoked
				// be the engine more than once --> only create the first time.
				if ( targetObject == null )
				{
					// Create persistent class using the dummy constructor. Actual
					// constructor contained in __saraswati_init() methods will be
					// invoked further down.
					Constructor< ? > constructor = ClassWrapper.getDeclaredConstructor( theClass, new Class< ? >[]{ __saraswati_dummy_type.class } );
					constructor.setAccessible( true );
					targetObject = newInstance( constructor, new __saraswati_dummy_type() );
					nextMiInstance.setObjectInstance( targetObject );
					adjustStackTraceIfThrowable( targetObject );
				}
			}
			else
			{
				// Create non-persistent class using specified constructor.
				// This is the return value and no further invocations are necessary.
				Constructor< ? > constructor = ClassWrapper.getDeclaredConstructor( theClass, paramTypes );
				constructor.setAccessible( true );
				IntegralHelper.adjustParameterTypes( constructor.getParameterTypes(), params );
				targetObject = newInstance( constructor, params );
				nextMiInstance.setObjectInstance( targetObject );
				invokeMethod = false;
				adjustStackTraceIfThrowable( targetObject );
			}
		}
		
		if ( invokeMethod )
		{
			// Otherwise, just invoke the method through reflection.
			Method method = ClassWrapper.getDeclaredMethod( theClass, methodName, paramTypes );
			method.setAccessible( true );
			IntegralHelper.adjustParameterTypes( method.getParameterTypes(), params );
			methodRetVal = invoke( method, targetObject, params );
		}

		Object retVal = null;
		
		if ( nextMiInstance.getRequiresObjectConstruction() )
			retVal = targetObject;
		else
			retVal = methodRetVal;

		return retVal;
	}
	
	private void adjustStackTraceIfThrowable( Object targetObject )
	{
		if ( targetObject instanceof Throwable )
			adjustStackTrace( (Throwable)targetObject );
	}
	
	private Object newInstance( Constructor< ? > constructor, Object ... initargs )
	{
		try
		{
			return constructor.newInstance( initargs );
		}
		// User exceptions.
		catch ( IllegalAccessException e )
		{
			throw new ProcessExecutionException( e );
		}
		catch ( InvocationTargetException e )
		{
			if ( e.getCause() != null )
				throw new ProcessExecutionException( e.getCause() );
			else if ( e.getTargetException() != null )
				throw new ProcessExecutionException( e.getTargetException() );
			else
				throw new IllegalStateException( "No cause and no target exception found for InvocationTargetException." );
		}
		// Engine errors.
		catch ( InstantiationException e )
		{
			throw new InstantiationExceptionWrapper( e );
		}
	}
	
	private Object invoke( Method method, Object targetObject, Object ... params )
	{
		// Note that a NullPointerException occurs when targetObject == null
		// and the method is a virtual method; when the method itself throws
		// a NullPointerException it is converted to a InvocationTargetException.
		// For our purposes, a NullPointerException is regarded as a user error,
		// since it can only occur when user code tries to dereference a null pointer.
		try
		{
			return method.invoke( targetObject, params );
		}
		// User exceptions.
		catch ( IllegalAccessException | NullPointerException e )
		{
			throw new ProcessExecutionException( e );
		}
		catch ( InvocationTargetException e )
		{
			if ( e.getCause() != null )
				throw new ProcessExecutionException( e.getCause() );
			else if ( e.getTargetException() != null )
				throw new ProcessExecutionException( e.getTargetException() );
			else
				throw new IllegalStateException( "No cause and no target exception found for InvocationTargetException." );
		}
	}

	private void setNextMethodInvocationInstance()
	{
		// If the stack is empty --> must be first invocation.
		// In this case, the next MII is already set.
		if ( getPFrameStack().isEmpty() )
		{
			pushPFrame();
		}
		// If we invoked another method --> extract the next MII.
		else if ( getLastMethodResultInstance().getInvokedOtherMethod() )
		{
			setNextMethodInvocationInstance( extractMethodInvocationInstance() );
			PFrame frameToAdjust = getCurrentPFrame();
			 
			adjustOperandStackForMethodInvocationInstance( frameToAdjust );
			
			// Only persistent methods have PFrames.
			if ( getNextMethodInvocationInstance().isResolvedClassPersistent() )
				pushPFrame();
		}
		// Otherwise, the last invocation was a final return -->
		// the next MII is the MII of the current frame.
		else
		{
			setNextMethodInvocationInstance( getCurrentPFrame().getMethodInvocationInstance() );
		}
	}

	private void adjustOperandStackForMethodInvocationInstance( PFrame pFrame )
	{
		ExecutionState executionState = pFrame.getExecutionState();
		Object[] operandStack = executionState.getOperandStack();
		int newStackSize = operandStack.length;
		
		if ( getNextMethodInvocationInstance().getObjectInstance() != null && !getNextMethodInvocationInstance().getRequiresObjectConstruction() )
			newStackSize -= 1;
		
		newStackSize -= getNextMethodInvocationInstance().getParameters().length;
		
		Object[] newOperandStack = Arrays.copyOf( operandStack, newStackSize );
		executionState.setOperandStack( newOperandStack );
	}
	
	public MethodInvocationInstance extractMethodInvocationInstance()
	{
		PProcessPMethodCommunicationArea comArea = getPProcessPMethodCommunicationArea();
		ExecutionState executionState = getPFrameStack().peek().getExecutionState();
	
		Object[] operandStack = executionState.getOperandStack();
		FQJVMMethodName invokedMethodName = comArea.getInvokedMethodName();
		MethodInvocationType invocationType = comArea.getMethodInvocationType();
		boolean requiresObjectConstruction = false;
		FQJVMMethodName newInvokedMethodName = new FQJVMMethodName( comArea.getInvokedMethodName() );
		
		if ( invokedMethodName.getJVMMethodName().equals( FieldAndMethodConstants.SARASWATI_NEW_MARKER_METHOD ) )
		{
			// Make sure class has been loaded at least once to 
			// ensure entries in SymbolTable.
			JavaClassHelper.getClassWithJVMName( invokedMethodName.getJVMClassName() );
			newInvokedMethodName.setJVMMethodName( FieldAndMethodConstants.INIT_METHOD );
			requiresObjectConstruction = true;
		}
		
		String[] toBeInvokedParamTypeNames = JVMDescriptorHelper.getParamTypesAsJavaClassNames( invokedMethodName.getJVMMethodDescriptor() );
		Object[] toBeInvokedMethodParams = new Object[ toBeInvokedParamTypeNames.length ];
	
		for ( int i = 0; i < toBeInvokedParamTypeNames.length; ++i )
			toBeInvokedMethodParams[ i ] = operandStack[ ( operandStack.length - toBeInvokedParamTypeNames.length ) + i ];

		Object toBeInvokedObject =  null;
		
		// Note: static methods and uninstrumented constructor invocations
		// do not have objects asssociated with them.
		if ( !( invocationType.equals( MethodInvocationType.INVOKE_STATIC ) || requiresObjectConstruction ) )
		{
			toBeInvokedObject = operandStack[ operandStack.length - toBeInvokedParamTypeNames.length - 1 ];
		}
		
		MethodInvocationInstance newMIInstance = new MethodInvocationInstance( invocationType, newInvokedMethodName, toBeInvokedObject, toBeInvokedMethodParams, requiresObjectConstruction );
		
		// reset invoked method
		invokedMethodName.setJVMClassName( null );
		invokedMethodName.setJVMMethodName( null );
		invokedMethodName.setJVMMethodDescriptor( null );
	
		return newMIInstance;
	}
	
	private void setMethodResultInstance( Object retVal, Throwable exception )
	{
		setLastMethodResultInstance( new MethodResultInstance( retVal, exception ) );
		
		// If the instrumented code did not specify a method to invoke --> this method
		// has completed. Otherwise, it is still executing.
		PProcessPMethodCommunicationArea commArea = getPProcessPMethodCommunicationArea();
		if ( commArea == null || commArea.getInvokedMethodName().getJVMClassName() == null || exception != null )
			getLastMethodResultInstance().setInvokedOtherMethod( false );
		else
			getLastMethodResultInstance().setInvokedOtherMethod( true );

		// Pop frame off of stack and adjust the operand
		// stack of the new topmost frame, if necessary.
		if ( !getLastMethodResultInstance().getInvokedOtherMethod() )
		{
			// Only persistent methods have PFrames.
			if ( getNextMethodInvocationInstance().isResolvedClassPersistent() )
				popPFrame();

			// If popPFrame() caused the process to enter the CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE
			// state --> return without further processing.
			if ( getSelfProxy().isInState( PersistentProcessStateMachine.CURRENT_EXECUTION_SEGMENT_TERMINATED_STATE ) )
				return;

			adjustOperandStackForMethodReturnInstance();
		}

		// If an exception occurred --> set flag in commArea to let instrumented code know.
		if ( getLastMethodResultInstance().getException() == null )
			commArea.setExceptionOccured( false );
		else
			commArea.setExceptionOccured( true );
	}
	
	private void adjustOperandStackForMethodReturnInstance()
	{
		String methodDescriptor = getNextMethodInvocationInstance().getFQMethodName().getJVMMethodDescriptor();
		Class< ? > returnType = JavaClassHelper.getReturnType( methodDescriptor );

		Object returnValue = getLastMethodResultInstance().getReturnValue();
		Throwable exception = getLastMethodResultInstance().getException();
		boolean invokedOtherMethod = getLastMethodResultInstance().getInvokedOtherMethod();
		boolean requiresObjectConstruction = getNextMethodInvocationInstance().getRequiresObjectConstruction();
		
		if ( returnValue != null )
			returnValue = IntegralHelper.adjustReturnType( returnType, returnValue );

		// If the was not the final return from this method
		// --> no need to adjust --> return.
		if ( invokedOtherMethod )
			return;
		
		// If there is neither a return value nor an exception AND if
		// this is not an object construction (which, technically, has
		// as return type of void, but is handled as if returning an
		// instance of the instantiated class by this engine) -->
		// the operand stack doesn't need to be adjusted --> return.
		if ( !requiresObjectConstruction && returnType.equals( void.class ) && exception == null )
			return;
		
		// If the call stack is empty --> nothing to adjust --> return.
		if ( getPFrameStack().isEmpty() )
			return;
		
		Object returnValueOrException = ( returnValue != null ? returnValue : exception );
		ExecutionState executionState = getCurrentPFrame().getExecutionState();
		Object[] newOperandStack = ArrayUtils.add( executionState.getOperandStack(), returnValueOrException );
		executionState.setOperandStack( newOperandStack );
	}
	
	private void popPFrame()
	{
		getPFrameStack().pop();
		getSelfProxy().notifyVolatilityChange( VolatilityChangeType.POP_FRAME );
		
		if ( getPFrameStack().isEmpty() )
		{
			getpProcessExecutionSegmentResult().setReturnValue( getLastMethodResultInstance().getReturnValue() );
			getpProcessExecutionSegmentResult().setException( getLastMethodResultInstance().getException() );
			popPProcessExecutionSegment();
		}
	}

	private void pushPFrame()
	{
		FQJVMMethodName fqMethodName = getNextMethodInvocationInstance().getFQMethodName();
		SymbolRegistry symbolRegistry = PEngine.getPEngine().getPClassLoader().getSymbolRegistry();
		String sourceFileName = symbolRegistry.getSourceFileName( fqMethodName.getJVMClassName() );
		String className = JVMDescriptorHelper.getFieldDescriptorAsJavaClassName( fqMethodName.getJVMClassName() );
		StackTraceElement pStackTraceElement = new StackTraceElement( className, fqMethodName.getJVMMethodName(), sourceFileName, -1 );
		PFrame pFrame = new PFrame( getNextMethodInvocationInstance(), pStackTraceElement, getTopFrameVolatility() );
		getPFrameStack().push( pFrame );
		getSelfProxy().notifyVolatilityChange( VolatilityChangeType.PUSH_FRAME );
	}
	
	///////////////////
	// EXTERNAL METHODS
	///////////////////
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public String[] getStates()
	{
		Set< String > states = persistentProcessStateMachine.getStates();
		
		return states.toArray( new String[ states.size() ] );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public String[] getLeafStates()
	{
		Set< String > states = persistentProcessStateMachine.getLeafStates();
		
		return states.toArray( new String[ states.size() ] );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public PProcessHandle getParent()
	{
		return PersistentProcessImpl.createPProcessHandleProxy( parentOID );
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS )
	public PProcessHandle[] getChildren()
	{
		PProcessHandle[] pProcessHandles = new PProcessHandle[ childOIDs.size() ];
		PProcessOID[] childOIDsAsArray = childOIDs.toArray( new PProcessOID[ childOIDs.size() ] );
		
		for ( int i = 0 ; i < pProcessHandles.length ; ++i )
			pProcessHandles[ i ] = PersistentProcessImpl.createPProcessHandleProxy( childOIDsAsArray[ i ] );
		
		return pProcessHandles;
	}
	
	/////////////////////
	// SUPPORTING METHODS
	/////////////////////
	
	protected PProcessExecutionSegment getCurrentPProcessExecutionSegment()
	{
		return pProcessExecutionSegmentStack.peek();
	}
	
	protected void pushPProcessExecutionSegment()
	{
		PProcessExecutionSegment execSegment = new PProcessExecutionSegment( new PFrameStack(), new PProcessPMethodCommunicationArea(), null, null );
		pProcessExecutionSegmentStack.push( execSegment );
		
		// The first segment gets pushed onto the stack before the process
		// is actually running; in that case, skip the state transition.
		if ( getSelfProxy().isInState( PersistentProcessStateMachine.NEW_STATE ) )
			return;
		
		signalEvent( PersistentProcessStateMachine.PUSH_SEGMENT_EVENT );
	}
	
	protected void popPProcessExecutionSegment()
	{
		pProcessExecutionSegmentStack.pop();
		signalEvent( PersistentProcessStateMachine.POP_SEGMENT_EVENT );
	}
	
	protected PFrameStack getPFrameStack()
	{
		return getCurrentPProcessExecutionSegment().getPFrameStack();
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public PFrame getCurrentPFrame()
	{
		PFrame pFrame = null;
		
		try
		{
			pFrame = getPFrameStack().peek();
		}
		catch ( EmptyStackException e )
		{
		}
		
		return pFrame;
	}

	protected MethodInvocationInstance getNextMethodInvocationInstance()
	{
		return  getCurrentPProcessExecutionSegment().getNextMethodInvocationInstance();
	}

	protected void setNextMethodInvocationInstance( MethodInvocationInstance nextMethodInvocationInstance )
	{
		getCurrentPProcessExecutionSegment().setNextMethodInvocationInstance( nextMethodInvocationInstance );
	}

	protected MethodResultInstance getLastMethodResultInstance()
	{
		return getCurrentPProcessExecutionSegment().getLastMethodResultInstance();
	}

	protected void setLastMethodResultInstance( MethodResultInstance lastMethodResultInstance )
	{
		getCurrentPProcessExecutionSegment().setLastMethodResultInstance( lastMethodResultInstance );
	}
	
	private Volatility getTopFrameVolatility()
	{
		Volatility topFrameVolatility = null;
		
		PFrame currentFrame = getCurrentPFrame();
		
		if ( currentFrame == null )
			topFrameVolatility = initialVolatilityActual;
		else
			topFrameVolatility = currentFrame.getVolatility();
		
		return topFrameVolatility;
	}
	
	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, executionStateProtection=EXECUTION_READ_LOCK )
	public void setLineNumber( int lineNumber )
	{
		StackTraceElement stackTraceElement = getCurrentPFrame().getStackTraceElement();
		Field field =  ClassWrapper.getDeclaredField( StackTraceElement.class, FieldAndMethodConstants.LINE_NUMBER_FIELD );
		field.setAccessible( true );
		FieldWrapper.set( field, stackTraceElement, lineNumber );
	}
	
	public static void setLineNumberOfCurrentProcess( int lineNumber )
	{
		getSelfProxyCurrentProcess().setLineNumber( lineNumber );
	}

	public static PFrame getCurrentPFrameOfCurrentProcess()
	{
		PersistentProcess pProcess = getSelfProxyCurrentProcess();
		PFrame pFrame = null;
		
		if ( pProcess != null )
			pFrame = pProcess.getCurrentPFrame();
		
		return pFrame;
	}
	
	public static PProcessPMethodCommunicationArea getPProcessPMethodCommunicationAreaOfCurrentProcess()
	{
		return getSelfProxyCurrentProcess().getPProcessPMethodCommunicationArea();
	}

	@Override
	public boolean isPersistent()
	{
		return true;
	}

	@Override
	public boolean isSwappable()
	{
		return true;
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PEventQueueOID getEventQueueOID()
	{
		return eventQueue.getOID();
	}

	@Override
	@ProxyMethod( synchronicity=SYNCHRONOUS, stateProtection=STATE_READ_LOCK )
	public PEventTopicOID getEventTopicOID()
	{
		return eventTopic.getOID();
	}

	/////////////////
	// CREATE METHODS
	/////////////////

	public static PProcessHandle create( Runnable runnable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion )
	{
		return create( runnable, null, name, initialVolatility, owner, autoCheckpointing, destroyOnCompletion );
	}
	
	public static PProcessHandle create( Runnable runnable, String name )
	{
		return create( runnable, null, name );
	}
	
	public static PProcessHandle create( Runnable runnable )
	{
		return create( runnable, null, null );
	}

	public static PProcessHandle create( Callable< ? > callable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion )
	{
		return create( null, callable, name, initialVolatility, owner, autoCheckpointing, destroyOnCompletion );
	}
	
	public static PProcessHandle create( Callable< ? > callable, String name )
	{
		return create( null, callable, name );
	}
	
	public static PProcessHandle create( Callable< ? > callable )
	{
		return create( null, callable );
	}
	
	public static PProcessHandle create()
	{
		return create( null, null, null );
	}

	public static PProcessHandle create( Runnable runnable, Callable< ? > callable )
	{
		return create( runnable, callable, null );
	}

	public static PProcessHandle create( Runnable runnable, Callable< ? > callable, String name )
	{
		return create( runnable, callable, name, null, null, PersistentProcessImpl.AUTO_CHECKPOINTING_DEFAULT, PersistentProcessImpl.DESTROY_ON_COMPLETION_DEFAULT );
	}
	
	private static PProcessHandle create( Runnable runnable, Callable< ? > callable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PProcessOID persistentProcessOID = omniObjectPool.createPersistentProcess( runnable, callable, name, initialVolatility, owner, autoCheckpointing, destroyOnCompletion );
		PProcessHandle pProcessHandle = createPProcessHandleProxy( persistentProcessOID );
		
		return pProcessHandle;
	}
	
	///////////////
	// FIND METHODS
	///////////////
	
	public static PProcessHandle findByOID( PProcessOID oid )
	{
		return createPProcessHandleProxy( oid );
	}
	
	@SuppressWarnings( "unchecked" )
	public static List< PProcessHandle > findByName( String name )
	{
		List< PProcessOID > oids = (List< PProcessOID >)(Object)findOIDsByName( name, PersistentProcessImpl.class );
		List< PProcessHandle > pProcessHandles = PersistentObjectListHelper.createExternalProxyList( PProcessHandle.class, oids );

		return pProcessHandles;
	}

	@SuppressWarnings( "unchecked" )
	public static List< PProcessHandle > findByCreateDate( Date createDateLowerBoundary, Date createDateUpperBoundary )
	{
		List< PProcessOID > oids = (List< PProcessOID >)(Object)findOIDsByCreateDate( createDateLowerBoundary, createDateUpperBoundary, PersistentProcessImpl.class );
		List< PProcessHandle > pProcessHandles = PersistentObjectListHelper.createExternalProxyList( PProcessHandle.class, oids );
		
		return pProcessHandles;
	}

	@SuppressWarnings( "unchecked" )
	public static List< PProcessHandle > findByLastActivityDate( Date lastActivityDateLowerBoundary, Date lastActivityDateUpperBoundary )
	{
		List< PProcessOID > oids = (List< PProcessOID >)(Object)findOIDsByLastActivityDate( lastActivityDateLowerBoundary, lastActivityDateUpperBoundary, PersistentProcessImpl.class );
		List< PProcessHandle > pProcessHandles = PersistentObjectListHelper.createExternalProxyList( PProcessHandle.class, oids );
		
		return pProcessHandles;
	}

	public static PProcessHandle findByOwnableObject( POwnableObjectOID ownableObjectOID )
	{
		PProcessOID owningProcessOID = findOIDByOwnableObject( ownableObjectOID );
		PProcessHandle pProcessHandle = createPProcessHandleProxy( owningProcessOID );
		
		return pProcessHandle;
	}
	
	public static List< PProcessOID > findByLastStableDate( Date lastStableDateLowerBoundary, Date lastStableDateUpperBoundary )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		List< PProcessOID > oids = omniObjectPool.findByLastStableDate( lastStableDateLowerBoundary, lastStableDateUpperBoundary );
		
		return oids;
	}

	public static List< PProcessHandle > findPProcessHandleByLastStableDate( Date lastStableDateLowerBoundary, Date lastStableDateUpperBoundary )
	{
		List< PProcessOID > oids = findByLastStableDate( lastStableDateLowerBoundary, lastStableDateUpperBoundary );
		List< PProcessHandle > pProcessHandles = PersistentObjectListHelper.createExternalProxyList( PProcessHandle.class, oids );
		
		return pProcessHandles;
	}

	public static PProcessOID findOIDByOwnableObject( POwnableObjectOID ownableObjectOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		PProcessOID owningProcessOID = omniObjectPool.findByOwnableObject( ownableObjectOID );
		
		return owningProcessOID;
	}
	
	////////////////
	// PROXY METHODS
	////////////////
	
	@Override
	public PersistentProcess getSelfProxy()
	{
		return (PersistentProcess)super.getSelfProxy();
	}

	@Override
	public PersistentProcess getOtherProxy()
	{
		return (PersistentProcess)super.getOtherProxy();
	}

	public static PersistentProcess getSelfProxyCurrentProcess()
	{
		PProcessOID pProcessOID = currentProcessOID.get();
		PersistentProcess selfProxy = null;
		
		if ( pProcessOID != null )
			selfProxy = (PersistentProcess)getProxy( ProxyType.INTERNAL_SELF, pProcessOID );
		
		return selfProxy;
	}

	public static PersistentProcess getOtherProxy( PProcessOID pProcessOID )
	{
		return (PersistentProcess)getProxy( ProxyType.INTERNAL_OTHER, pProcessOID );
	}
	
	@Override
	protected PersistentProcess createSelfProxy()
	{
		return createProxy( ProxyType.INTERNAL_SELF, getOID() );
	}

	@Override
	protected PersistentProcess createOtherProxy()
	{
		return createProxy( ProxyType.INTERNAL_OTHER, getOID() );
	}
	
	public static PProcess createPProcessProxy()
	{
		return createProxy( ProxyType.EXTERNAL_SELF, currentProcessOID.get() );
	}
	
	public static PProcessHandle createPProcessHandleProxy( PProcessOID pProcessOID )
	{
		return createProxy( ProxyType.EXTERNAL_OTHER, pProcessOID );
	}
	
	private static < T > T createProxy( ProxyType proxyType, PProcessOID pProcessOID )
	{
		return createProxy( proxyType, pProcessOID, PersistentProcess.class, PProcess.class, PProcessHandle.class, new PersistentProcessInvocationHandler() );
	}

	@Override
	public PProcessOID getOID()
	{
		return (PProcessOID)super.getOID();
	}
	
	public static PProcessHandle getHandle( PProcessOID pProcessOID )
	{
		return createPProcessHandleProxy( pProcessOID );
	}
	
	public static PProcess getCurrentProcess()
	{
		return createPProcessProxy();
	}
}
