package ch.shaktipat.saraswati.internal.web.view.peventtopic;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.model.peventtopic.PEventTopicListRowModel;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.ListPageContent;


public class PEventTopicListPageContent extends ListPageContent
{
	public PEventTopicListPageContent( Controller controller )
	{
		super( controller );
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
		PEventTopicListRowModel pRow = (PEventTopicListRowModel)row;
		
		String oid = Transformation.getString( PObjectOIDTransformer.class, pRow.getOid() );

		Writer.write( "<td>" + oid + "</td>" );
		Writer.write( "<td>" + pRow.getName() + "</td>" );
		Writer.write( "<td>" + Transformation.getString( DateTransformer.class, pRow.getCreateDate() ) + "</td>" );
	}

	@Override
	protected Class< ? > getDetailPageClass()
	{
		return PEventTopicDetailPage.class;
	}
}
