package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public class PersistentEventTopicInvoker extends PersistentObjectInvoker
{
	public PersistentEventTopicInvoker( PEventTopicOID targetOID, MemoryModel memoryModel )
	{
		super( targetOID, memoryModel );
	}

	@Override
	public PEventTopicOID getTargetOID()
	{
		return (PEventTopicOID)super.getTargetOID();
	}
}
