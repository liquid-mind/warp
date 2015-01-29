package ch.shaktipat.saraswati.pobject.oid;

import java.io.Serializable;
import java.util.UUID;

// TODO Rename all OID classes to ID, since O is redundant...
public abstract class PObjectOID implements Serializable
{
	private static final long serialVersionUID = 1L;

	private UUID oid;

	public PObjectOID()
	{
		this( UUID.randomUUID() );
	}

	public PObjectOID( String oidAsString )
	{
		super();
		this.oid = UUID.fromString( oidAsString );
	}

	public PObjectOID( UUID oid )
	{
		super();
		this.oid = oid;
	}

	@Override
	public String toString()
	{
		return oid.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( oid == null ) ? 0 : oid.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		PObjectOID other = (PObjectOID)obj;
		if ( oid == null )
		{
			if ( other.oid != null )
				return false;
		}
		else if ( !oid.equals( other.oid ) )
			return false;
		return true;
	}
}
