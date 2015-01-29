package ch.shaktipat.saraswati.pobject.oid;

import java.util.UUID;

public class PEventQueueOID extends POwnableObjectOID
{
	private static final long serialVersionUID = 1L;

	public PEventQueueOID()
	{
		super();
	}

	public PEventQueueOID( String oidAsString )
	{
		super( oidAsString );
	}

	public PEventQueueOID( UUID oid )
	{
		super( oid );
	}
}
