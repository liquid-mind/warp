package ch.shaktipat.saraswati.internal.web.model;

import java.util.Date;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class POwnableObjectModel extends PObjectModel
{
	private PProcessOID owningProcessOID;
	private String owningProcessName;
	
	public POwnableObjectModel(
			boolean commandSuccess, 
			PObjectOID oid,
			String name,
			Date createDate,
			PProcessOID owningProcessOID,
			String owningProcessName )
	{
		super( commandSuccess, oid, name, createDate );
		this.owningProcessOID = owningProcessOID;
		this.owningProcessName = owningProcessName;
	}

	public PProcessOID getOwningProcessOID()
	{
		return owningProcessOID;
	}

	public void setOwningProcessOID( PProcessOID owningProcessOID )
	{
		this.owningProcessOID = owningProcessOID;
	}

	public String getOwningProcessName()
	{
		return owningProcessName;
	}

	public void setOwningProcessName( String owningProcessName )
	{
		this.owningProcessName = owningProcessName;
	}
}
