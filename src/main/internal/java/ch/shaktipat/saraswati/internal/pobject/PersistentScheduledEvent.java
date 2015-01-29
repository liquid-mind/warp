package ch.shaktipat.saraswati.internal.pobject;

import java.util.Date;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public interface PersistentScheduledEvent extends PersistentOwnableObject
{
	@Override
	public PScheduledEventOID getOID();
	public Event getEvent();
	public Date getDeadline();
	public PEventQueueOID getEventQueueOID();
	@Override
	public PersistentScheduledEvent getSelfProxy();
	@Override
	public PersistentScheduledEvent getOtherProxy();
}
