package ch.shaktipat.saraswati.internal.web.view.pprocess;

import java.security.Principal;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessListRowModel;
import ch.shaktipat.saraswati.internal.web.transform.DateTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.PrincipalTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.ListPageContent;


public class PProcessListPageContent extends ListPageContent
{
	public PProcessListPageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderTableHeaderRow()
	{
		Writer.write( "<th>OID</th>" );
		Writer.write( "<th>Name</th>" );
		Writer.write( "<th>Owner</th>" );
		Writer.write( "<th>Create Date</th>" );
		Writer.write( "<th>State</th>" );
		Writer.write( "<th>Volatility</th>" );
	}

	@Override
	protected void renderTableDataRow( PObjectModel row )
	{
		PProcessListRowModel pRow = (PProcessListRowModel)row;
		
		String oid = Transformation.getString( PObjectOIDTransformer.class, pRow.getOid() );
		Principal principal = pRow.getOwner();

		Writer.write( "<td>" + oid + "</td>" );
		Writer.write( "<td>" + pRow.getName() + "</td>" );
		Writer.write( "<td>" + ( principal == null ? "NA" : Transformation.getString( PrincipalTransformer.class, principal ) ) + "</td>" );
		Writer.write( "<td>" + Transformation.getString( DateTransformer.class, pRow.getCreateDate() ) + "</td>" );
		Writer.write( "<td>" + pRow.getState() + "</td>" );
		Writer.write( "<td>" + pRow.getVolatility() + "</td>" );
	}

	@Override
	protected Class< ? > getDetailPageClass()
	{
		return PProcessDetailPage.class;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected ListModel< PProcessListRowModel > getModel()
	{
		return super.getModel();
	}
}
