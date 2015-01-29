package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;


public class PersistentEventQueueInvocationHandler extends PersistentObjectInvocationHandler
{
	private static final long serialVersionUID = 1L;
	
	public PersistentEventQueueInvocationHandler()
	{
		super();
	}

	@Override
	protected PersistentObjectInvoker createPersistentObjectInvoker()
	{
		return new PersistentEventQueueInvoker( getTargetOID(), getMemoryModel() );
	}

	@Override
	public PEventQueueOID getTargetOID()
	{
		return (PEventQueueOID)super.getTargetOID();
	}
}
