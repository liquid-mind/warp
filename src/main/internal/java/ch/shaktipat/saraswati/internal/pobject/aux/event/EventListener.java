package ch.shaktipat.saraswati.internal.pobject.aux.event;

import java.io.Serializable;

import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class EventListener implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private PProcessOID targetProcessOID;
	private PScheduledEventOID scheduledTimeoutEventOID;
	private EventFilter eventFilter;
	
	public EventListener( PProcessOID targetProcessOID, PScheduledEventOID scheduledTimeoutEventOID, EventFilter eventFilter )
	{
		super();
		this.targetProcessOID = targetProcessOID;
		this.scheduledTimeoutEventOID = scheduledTimeoutEventOID;
		this.eventFilter = eventFilter;
	}

	public PProcessOID getTargetProcessOID()
	{
		return targetProcessOID;
	}

	public void setTargetProcessOID( PProcessOID targetProcessOID )
	{
		this.targetProcessOID = targetProcessOID;
	}

	public PScheduledEventOID getScheduledTimeoutEventOID()
	{
		return scheduledTimeoutEventOID;
	}

	public void setScheduledTimeoutEventOID( PScheduledEventOID scheduledTimeoutEventOID )
	{
		this.scheduledTimeoutEventOID = scheduledTimeoutEventOID;
	}

	public EventFilter getEventFilter()
	{
		return eventFilter;
	}

	public void setEventFilter( EventFilter eventFilter )
	{
		this.eventFilter = eventFilter;
	}

	@Override
	public String toString()
	{
		return "EventListener [targetProcessOID=" + targetProcessOID + ", scheduledTimeoutEventOID=" + scheduledTimeoutEventOID + ", eventFilter=" + eventFilter + "]";
	}
}
