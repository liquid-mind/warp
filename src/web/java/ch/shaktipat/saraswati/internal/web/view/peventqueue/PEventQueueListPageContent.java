package ch.shaktipat.saraswati.internal.web.view.peventqueue;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.model.peventqueue.PEventQueueListRowModel;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.ListPageContent;


public class PEventQueueListPageContent extends ListPageContent
{

	public PEventQueueListPageContent( Controller controller )
	{
		super( controller );
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void renderTableHeaderRow()
	{
		Writer.write( "<th>OID</th>" );
		Writer.write( "<th>Name</th>" );
		Writer.write( "<th>Create Date</th>" );
	}

	@Override
	protected void renderTableDataRow( PObjectModel row )
	{
		PEventQueueListRowModel pRow = (PEventQueueListRowModel)row;
		
		String oid = Transformation.getString( PObjectOIDTransformer.class, pRow.getOid() );

		Writer.write( "<td>" + oid + "</td>" );
		Writer.write( "<td>" + pRow.getName() + "</td>" );
		Writer.write( "<td>" + Transformation.getString( DateTransformer.class, pRow.getCreateDate() ) + "</td>" );
	}

	@Override
	protected Class< ? > getDetailPageClass()
	{
		return PEventQueueDetailPage.class;
	}
}
