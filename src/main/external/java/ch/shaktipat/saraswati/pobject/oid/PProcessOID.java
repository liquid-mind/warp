package ch.shaktipat.saraswati.pobject.oid;

import java.util.UUID;

public class PProcessOID extends PObjectOID
{
	private static final long serialVersionUID = 1L;

	public PProcessOID()
	{
		super();
	}

	public PProcessOID( String oidAsString )
	{
		super( oidAsString );
	}

	public PProcessOID( UUID oid )
	{
		super( oid );
	}
}
