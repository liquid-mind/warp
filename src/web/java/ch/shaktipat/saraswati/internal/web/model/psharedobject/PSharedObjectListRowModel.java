package ch.shaktipat.saraswati.internal.web.model.psharedobject;

import java.util.Date;

import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PSharedObjectListRowModel extends PObjectModel
{
	public PSharedObjectListRowModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate )
	{
		super( commandSuccess, oid, name, createDate );
	}
}
