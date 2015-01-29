package ch.shaktipat.saraswati.pobject;

import java.util.Date;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public interface PScheduledEvent extends POwnableObject
{
	@Override
	public PScheduledEventOID getOID();
	public Event getEvent();
	public Date getDeadline();
	public PEventQueue getEventListener();
}
