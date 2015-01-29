package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PProcessOID;


public class PersistentProcessInvocationHandler extends PersistentObjectInvocationHandler
{
	private static final long serialVersionUID = 1L;
	
	public PersistentProcessInvocationHandler()
	{
		super();
	}

	@Override
	protected PersistentObjectInvoker createPersistentObjectInvoker()
	{
		return new PersistentProcessInvoker( getTargetOID(), getMemoryModel() );
	}

	@Override
	public PProcessOID getTargetOID()
	{
		return (PProcessOID)super.getTargetOID();
	}
}
