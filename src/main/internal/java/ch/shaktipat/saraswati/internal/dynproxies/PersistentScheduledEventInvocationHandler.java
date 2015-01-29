package ch.shaktipat.saraswati.internal.dynproxies;

import ch.shaktipat.saraswati.pobject.oid.PScheduledEventOID;


public class PersistentScheduledEventInvocationHandler extends PersistentObjectInvocationHandler
{
	private static final long serialVersionUID = 1L;
	
	public PersistentScheduledEventInvocationHandler()
	{
		super();
	}

	@Override
	protected PersistentObjectInvoker createPersistentObjectInvoker()
	{
		return new PersistentScheduledEventInvoker( getTargetOID(), getMemoryModel() );
	}

	@Override
	public PScheduledEventOID getTargetOID()
	{
		return (PScheduledEventOID)super.getTargetOID();
	}
}
