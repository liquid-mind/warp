package ch.shaktipat.saraswati.internal.pobject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.internal.pobject.aux.event.EventListener;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public interface PersistentEventQueue extends PersistentOwnableObject
{
	@Override
	public PEventQueueOID getOID();
	public void send( Event event );
	public Event listen();
	public Event listen( long time, TimeUnit timeUnit );
	public Event listen( Date deadline );
	public Event tryListen();
	public Event listen( Volatility volatility );
	public Event listen( Volatility volatility, long time, TimeUnit timeUnit );
	public Event listen( Volatility volatility, Date deadline );
	public Event listen( String filterExpression );
	public Event listen( String filterExpression, long time, TimeUnit timeUnit );
	public Event listen( String filterExpression, Date deadline );
	public Event tryListen( String filterExpression );
	public Event listen( Volatility volatility, String filterExpression );
	public Event listen( Volatility volatility, String filterExpression, long time, TimeUnit timeUnit );
	public Event listen( Volatility volatility, String filterExpression, Date deadline );
	public List< Class< ? > > getQueuedEventTypes();
	public void registerListener( PProcessOID targetProcessOID, String filterExpression, TimeSpecification timeSpecification );
	public boolean unregisterListener();
	public EventListener getListener();
	public void handleTimeoutEvent( PScheduledEventOID pScheduledEventOID );
	@Override
	public PersistentEventQueue getSelfProxy();
	@Override
	public PersistentEventQueue getOtherProxy();
}
