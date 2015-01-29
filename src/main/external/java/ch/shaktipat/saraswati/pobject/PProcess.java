package ch.shaktipat.saraswati.pobject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.volatility.VolatileResource;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PProcess extends PProcessCommon
{
	public Volatility getVolatility();
	public VolatileResource createVolatileResource();	
	public VolatileResource destroyVolatileResource();
	public void sleep( long time, TimeUnit timeUnit );
	public void sleep( Date deadline );
	public void sleep( Volatility volatility, long time, TimeUnit timeUnit );
	public void sleep( Volatility volatility, Date deadline );
	public void setCheckpoint();
	public PProcessHandle getPProcessHandle();
	public Event listen();
	public Event listen( long time, TimeUnit timeUnit );
	public Event listen( Date deadline );
	public Event tryListen();
	public Event listen( String filterExpression );
	public Event listen( String filterExpression, long time, TimeUnit timeUnit );
	public Event listen( String filterExpression, Date deadline );
	public Event tryListen( String filterExpression );
	public Event listen( Volatility volatility );
	public Event listen( Volatility volatility, long time, TimeUnit timeUnit );
	public Event listen( Volatility volatility, Date deadline );
	public Event listen( Volatility volatility, String filterExpression );
	public Event listen( Volatility volatility, String filterExpression, long time, TimeUnit timeUnit );
	public Event listen( Volatility volatility, String filterExpression, Date deadline );
}
