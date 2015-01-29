package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;


public class PersistentEventTopicInvocationHandler extends PersistentObjectInvocationHandler
{
	private static final long serialVersionUID = 1L;
	
	public PersistentEventTopicInvocationHandler()
	{
		super();
	}

	@Override
	protected PersistentObjectInvoker createPersistentObjectInvoker()
	{
		return new PersistentEventTopicInvoker( getTargetOID(), getMemoryModel() );
	}

	@Override
	public PEventTopicOID getTargetOID()
	{
		return (PEventTopicOID)super.getTargetOID();
	}
}
