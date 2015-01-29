package ch.shaktipat.saraswati.pobject.oid;

import java.util.UUID;

public abstract class POwnableObjectOID extends PObjectOID
{
	private static final long serialVersionUID = 1L;

	public POwnableObjectOID()
	{
		super();
	}

	public POwnableObjectOID( String oidAsString )
	{
		super( oidAsString );
	}

	public POwnableObjectOID( UUID oid )
	{
		super( oid );
	}
}
