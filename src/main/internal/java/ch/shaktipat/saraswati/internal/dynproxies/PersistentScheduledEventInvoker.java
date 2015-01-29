package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;

public class PersistentScheduledEventInvoker extends PersistentObjectInvoker
{
	public PersistentScheduledEventInvoker( PScheduledEventOID targetOID, MemoryModel memoryModel )
	{
		super( targetOID, memoryModel );
	}

	@Override
	public PScheduledEventOID getTargetOID()
	{
		return (PScheduledEventOID)super.getTargetOID();
	}
}
