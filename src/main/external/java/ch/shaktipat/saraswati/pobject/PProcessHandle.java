package ch.shaktipat.saraswati.pobject;

import java.security.Principal;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.volatility.Volatility;

public interface PProcessHandle extends PProcessCommon
{
	public void start();
	public void suspend();
	public void resume();
	public void cancel();
	public void join();
	public void join( long time, TimeUnit timeUnit );
	public void join( Date deadline );
	public void join( Volatility volatility );
	public void join( Volatility volatility, long time, TimeUnit timeUnit );
	public void join( Volatility volatility, Date deadline );
	public void setInitialVolatility( Volatility initialVolatility );
	public < T > Future< T > getFuture();
	public void setDestroyOnCompletion( boolean destroyOnCompletion );
	public boolean getDestroyOnCompletion();
	public void setAutoCheckpointing( boolean autoCheckpointing );
	public boolean getAutoCheckpointing();
	public void setName( String name );
	public void setOwningPrincipal( Principal owner );
	public void setRunnable( Runnable runnable );
	public void setCallable( Callable< ? > callable );
}
