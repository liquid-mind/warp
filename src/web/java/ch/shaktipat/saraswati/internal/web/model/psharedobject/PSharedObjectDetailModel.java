package ch.shaktipat.saraswati.internal.web.model.psharedobject;

import java.util.Date;

import ch.shaktipat.saraswati.internal.web.model.POwnableObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PSharedObjectDetailModel extends POwnableObjectModel
{
	private Class< ? > sharedObjectType;

	public PSharedObjectDetailModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate, PProcessOID owningProcessOID, String owningProcessName, Class< ? > sharedObjectType )
	{
		super( commandSuccess, oid, name, createDate, owningProcessOID, owningProcessName );
		this.sharedObjectType = sharedObjectType;
	}

	public PSharedObjectDetailModel( boolean commandSuccess )
	{
		super( commandSuccess, null, null, null, null, null );
	}

	public Class< ? > getSharedObjectType()
	{
		return sharedObjectType;
	}

	public void setSharedObjectType( Class< ? > sharedObjectType )
	{
		this.sharedObjectType = sharedObjectType;
	}
}
