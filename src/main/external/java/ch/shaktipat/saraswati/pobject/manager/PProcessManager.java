package ch.shaktipat.saraswati.pobject.manager;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PProcessManager extends PObjectManager
{
	// Create methods.
	public PProcessHandle create( Runnable runnable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion );
	public PProcessHandle create( Runnable runnable, String name );
	public PProcessHandle create( Runnable runnable );
	public PProcessHandle create( Callable< ? > callable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion );
	public PProcessHandle create( Callable< ? > callable, String name );
	public PProcessHandle create( Callable< ? > callable );
	public PProcessHandle create();
	
	// Find methods.
	public PProcessHandle findByOID( PProcessOID oid );	
	public List< PProcessHandle > findByName( String name );	
	public List< PProcessHandle > findByCreateDate( Date createDateLowerBoundary, Date createDateUpperBoundary );	
	public List< PProcessHandle > findByLastActivityDate( Date lastActivityDateLowerBoundary, Date lastActivityDateUpperBoundary );	
	public List< PProcessHandle > findByLastStableDate( Date lastStableDateLowerBoundary, Date lastStableDateUpperBoundary );	
	
	// Other methods.
	public PProcessHandle getHandle( PProcessOID pProcessOID );
	public PProcess getCurrentProcess();
	public Logger getSystemLogger( String name );
	public Logger getSystemLogger( String name, boolean useParentHandlers );
	public Logger getApplicationLogger( String name );
	public Logger getApplicationLogger( String name, boolean useParentHandlers );
}
