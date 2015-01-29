package ch.shaktipat.saraswati.internal.common;

import java.io.Serializable;
import java.util.UUID;

public class SaraswatiUniqueID implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID uniqueID;

	public SaraswatiUniqueID()
	{
		uniqueID = UUID.randomUUID();
	}
	
	public SaraswatiUniqueID( UUID uniqueID )
	{
		super();
		this.uniqueID = uniqueID;
	}
	
	public SaraswatiUniqueID( String uniqueIDAsString )
	{
		super();
		this.uniqueID = UUID.fromString( uniqueIDAsString );
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( uniqueID == null ) ? 0 : uniqueID.hashCode() );
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
		SaraswatiUniqueID other = (SaraswatiUniqueID)obj;
		if ( uniqueID == null )
		{
			if ( other.uniqueID != null )
				return false;
		}
		else if ( !uniqueID.equals( other.uniqueID ) )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return uniqueID.toString();
	}
}
