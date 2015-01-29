package ch.shaktipat.saraswati.internal.web.model;

import java.util.Date;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PObjectModel extends Model
{
	private PObjectOID oid;
	private String name;
	private Date createDate;
	
	public PObjectModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate )
	{
		super( commandSuccess );
		this.oid = oid;
		this.name = name;
		this.createDate = createDate;
	}

	public PObjectOID getOid()
	{
		return oid;
	}

	public void setOid( PObjectOID oid )
	{
		this.oid = oid;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate( Date createDate )
	{
		this.createDate = createDate;
	}
}
