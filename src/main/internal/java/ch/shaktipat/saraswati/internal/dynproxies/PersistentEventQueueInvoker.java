package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public class PersistentEventQueueInvoker extends PersistentObjectInvoker
{
	public PersistentEventQueueInvoker( PEventQueueOID targetOID, MemoryModel memoryModel )
	{
		super( targetOID, memoryModel );
	}

	@Override
	public PEventQueueOID getTargetOID()
	{
		return (PEventQueueOID)super.getTargetOID();
	}
}
