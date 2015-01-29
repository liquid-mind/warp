package ch.shaktipat.saraswati.pobject;

import java.security.Principal;
import java.util.UUID;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PProcessCommon extends PObject
{
	@Override
	public PProcessOID getOID();
	public Volatility getInitialVolatility();
	public String[] getStates();
	public String[] getLeafStates();
	public boolean isInState( String state );
	public Principal getOwningPrincipal();
	public PProcessHandle getParent();
	public PProcessHandle[] getChildren();
	public UUID subscribe( PEventQueueOID targetQueueOID, String filterExpr, boolean singleEventSubscription );
	public boolean unsubscribe( UUID subscriptionOID );
	public void send( Event event );
	public String getSystemLog();
	public String getApplicationLog();
	public POwnableObject[] getOwnableObjects();
	public StackTraceElement[] getStackTrace();
	public Class< ? > getRunnableType();
	public Class< ? > getCallableType();
}
