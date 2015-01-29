package ch.shaktipat.saraswati.internal.pobject;

import java.security.Principal;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl.VolatilityChangeType;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PFrame;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegment;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegmentResult;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessPMethodCommunicationArea;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.VolatileResource;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PersistentProcess extends PersistentObject
{
	// Shared with external
	@Override
	public PProcessOID getOID();
	public String[] getStates();
	public String[] getLeafStates();
	public Principal getOwningPrincipal();
	public StackTraceElement[] getStackTrace();
	public void setCheckpoint();
	public boolean getDestroyOnCompletion();
	public boolean getAutoCheckpointing();
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription );
	public boolean unsubscribe( UUID subscriptionID );
	public void send( Event event );

	// Volatility methods
	public Volatility getInitialVolatility();
	public void setInitialVolatility( Volatility initialVolatility );
	public VolatileResource createVolatileResource();
	public VolatileResource destroyVolatileResource();
	public Volatility getVolatility();
	public Volatility getVolatileResourcesVolatility();
	
	// Internal
	public Set< POwnableObjectOID > getOwnableObjectOIDs();
	public void addOwnableObjectOID( POwnableObjectOID ownableObjectOID );
	public void removeOwnableObjectOID( POwnableObjectOID ownableObjectOID );
	public ReadLock getExecutionStateReadLock();
	public WriteLock getExecutionStateWriteLock();
	public Runnable getRunnable();
	public Callable< ? > getCallable();
	public PProcessOID getParentOID();
	public Stack< PProcessExecutionSegment > getpProcessExecutionSegmentStack();
	public PProcessExecutionSegmentResult getpProcessExecutionSegmentResult();
	public Set< PProcessOID > getChildOIDs();
	public void addChildOID( PProcessOID childOID );
	public void fireEvent( String event );
	public boolean isInState( String state );
	public boolean getRequestedSuspension();
	public void setRequestSuspension( boolean requestSuspension );
	public boolean getRequestCancellation();
	public void setRequestCancellation( boolean requestCancellation );
	public void notifyVolatilityChange( VolatilityChangeType changeType );
	@Override
	public PersistentProcess getSelfProxy();
	@Override
	public PersistentProcess getOtherProxy();
	public String getSystemLog();
	public void setSystemLog( String systemLog );
	public String getApplicationLog();
	public void setApplicationLog( String applicationLog );
	public void setupInitialVolatilityActual( Volatility currentVolatility );
	
	// Internal (execution)
	public void startProcess( Volatility currentVolatility );
	public void terminateProcess( Throwable t );
	public void handleEvent( Event event );
	public void handleEventPrepareProcessToContinue( Event event );
	public void resumeProcess();
	public void invokeTestMethod( Volatility currentVolatility, String testClassName, String methodName, Class< ? >[] paramTypes, Object[] params );
	public PProcessExecutionSegmentResult invokeStaticIntializer( Class< ? > theClass ) throws ClassNotFoundException;
	public void setLineNumber( int lineNumber );
	public PFrame getCurrentPFrame();
	public PProcessPMethodCommunicationArea getPProcessPMethodCommunicationArea();
	public PEventQueueOID getEventQueueOID();
	public PEventTopicOID getEventTopicOID();
	public void checkForSuspensionOrCancellation();
}
