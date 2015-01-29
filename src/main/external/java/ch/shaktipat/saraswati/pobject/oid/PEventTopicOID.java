package ch.shaktipat.saraswati.pobject.oid;

import java.util.UUID;

public class PEventTopicOID extends POwnableObjectOID
{
	private static final long serialVersionUID = 1L;

	public PEventTopicOID()
	{
		super();
	}

	public PEventTopicOID( String oidAsString )
	{
		super( oidAsString );
	}

	public PEventTopicOID( UUID oid )
	{
		super( oid );
	}
}
