package ch.shaktipat.saraswati.pobject.oid;

import java.util.UUID;

public class PSharedObjectOID extends POwnableObjectOID
{
	private static final long serialVersionUID = 1L;

	public PSharedObjectOID()
	{
		super();
	}

	public PSharedObjectOID( String oidAsString )
	{
		super( oidAsString );
	}

	public PSharedObjectOID( UUID oid )
	{
		super( oid );
	}
}
