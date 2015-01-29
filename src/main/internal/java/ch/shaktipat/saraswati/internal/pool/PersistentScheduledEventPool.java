package ch.shaktipat.saraswati.internal.pool;

import java.util.List;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.common.TimeSpecification;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public interface PersistentScheduledEventPool extends PersistentObjectPool
{
	public PScheduledEventOID createScheduledEvent( String name, Event event, TimeSpecification timeSpecification, PEventQueueOID eventQueueOID );
	public List< PScheduledEventOID > findByEarliestEvent();
}
