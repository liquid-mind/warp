package ch.shaktipat.saraswati.internal.web.model.peventqueue;

import java.util.Date;

import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PEventQueueListRowModel extends PObjectModel
{
	public PEventQueueListRowModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate )
	{
		super( commandSuccess, oid, name, createDate );
	}
}
