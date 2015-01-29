package ch.shaktipat.saraswati.internal.pool;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PersistentProcessPool extends PersistentObjectPool
{
	public PProcessOID createPersistentProcess( Runnable runnable, Callable< ? > callable, String name, Volatility initialVolatility, Principal owner, boolean autoCheckpointing, boolean destroyOnCompletion );
	public List< PProcessOID > findByPersistentProcessState( String state );
	public List< PProcessOID > findByLastStableDate( Date lastStableDateLowerBoundary, Date lastStableDateUpperBoundary );
	public List< PProcessOID > findByVolatileObjectState( String state );
}
