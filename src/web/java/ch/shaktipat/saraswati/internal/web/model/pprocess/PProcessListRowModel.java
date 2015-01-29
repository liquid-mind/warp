package ch.shaktipat.saraswati.internal.web.model.pprocess;

import java.security.Principal;
import java.util.Date;

import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PProcessListRowModel extends PObjectModel
{
	private Principal owner;
	private String state;
	private String volatility;

	public PProcessListRowModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate, Principal owner, String state, String volatility )
	{
		super( commandSuccess, oid, name, createDate );
		this.owner = owner;
		this.state = state;
		this.volatility = volatility;
	}

	public Principal getOwner()
	{
		return owner;
	}

	public void setOwner( Principal owner )
	{
		this.owner = owner;
	}

	public String getState()
	{
		return state;
	}

	public void setState( String state )
	{
		this.state = state;
	}

	public String getVolatility()
	{
		return volatility;
	}

	public void setVolatility( String volatility )
	{
		this.volatility = volatility;
	}

	@Override
	public PProcessOID getOid()
	{
		return (PProcessOID)super.getOid();
	}

	public void setOid( PProcessOID oid )
	{
		super.setOid( oid );
	}

	@Override
	public String toString()
	{
		return "PProcessListRowModel [owner=" + owner + ", state=" + state + ", volatility=" + volatility + ", getOid()=" + getOid() + ", getName()=" + getName() + ", getCreateDate()="
				+ getCreateDate() + "]";
	}
}
