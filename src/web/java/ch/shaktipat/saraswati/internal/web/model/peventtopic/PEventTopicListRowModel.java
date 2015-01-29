package ch.shaktipat.saraswati.internal.web.model.peventtopic;

import java.util.Date;

import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PEventTopicListRowModel extends PObjectModel
{
	public PEventTopicListRowModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate )
	{
		super( commandSuccess, oid, name, createDate );
	}
}
