package ch.shaktipat.saraswati.internal.web.view;

import java.util.HashMap;
import java.util.Map;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.ListModel;
import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.internal.web.transform.PObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessListPage;

public abstract class ListPageContent extends Content
{
	public static final String OID_PARAM = "oid";
	public static final String SEARCH_PARAM = "search";
	public static final String STATE_PARAM = "state";
	public static final String ACTIVE_STATE = "active";
	public static final String PASSIVE_STATE = "passive";
	
	public ListPageContent( Controller controller )
	{
		super( controller );
	}
	
	@Override
	public void render()
	{
		renderSearchForm();

		Writer.write( "<p></p>" );

		ListModel< PObjectModel > model = getModel();
		
		if ( model.size() > 0 )
			renderTable();
		else
			renderEmptySearchResults();
	}
	
	private void renderEmptySearchResults()
	{
		Writer.write( "<div class='row'>" );
		Writer.write( "<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12'>" );
		Writer.write( "<div class='alert alert-info'>No data to display.</div>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
	}

	private void renderSearchForm()
	{
		String state = SaraswatiServletContext.getParameter( STATE_PARAM );
		Map< String, String[] > buttonMap = new HashMap< String, String[] >();
		String[] inactiveButton = new String[] { "", "" };
		String[] activeButton = new String[] { " active", " checked='checked'" };

		buttonMap.put( ACTIVE_STATE, inactiveButton );
		buttonMap.put( PASSIVE_STATE, inactiveButton );
		
		if ( state.equals( ACTIVE_STATE ) || state.isEmpty() )
			buttonMap.put( ACTIVE_STATE, activeButton );
		else if ( state.equals( PASSIVE_STATE ) )
			buttonMap.put( PASSIVE_STATE, activeButton );
		else
			throw new RuntimeException( "Invalid value for state: " + state );

		Writer.write( "<form method='get' action='" + SaraswatiServlet.getPageURL( PProcessListPage.class ) + "'>" );
		Writer.write( "<div class='row'>" );
		
		Writer.write( "<div class='col-lg-10 col-md-9 col-sm-9'>" );
		Writer.write( "<div class='input-group'>" );
		Writer.write( "<input type='text' class='form-control' placeholder='Search' name='" + SEARCH_PARAM + "' value='" + SaraswatiServletContext.getParameter( SEARCH_PARAM ) + "'>" );
		Writer.write( "<div class='input-group-btn'>" );
		Writer.write( "<button class='btn btn-default' type='submit'><i class='glyphicon glyphicon-search'></i></button>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
		
		Writer.write( "<div class='col-lg-2 col-md-3 col-sm-3'>" );
		Writer.write( "<div class='btn-group' data-toggle='buttons'>" );
		Writer.write( "<label class='btn btn-primary" + buttonMap.get( ACTIVE_STATE )[ 0 ] + "'>" );
		Writer.write( "<input type='radio' name='" + STATE_PARAM + "' value='" + ACTIVE_STATE + "'" + buttonMap.get( ACTIVE_STATE )[ 1 ] + ">Active</input>" );
		Writer.write( "</label>" );
		Writer.write( "<label class='btn btn-primary" + buttonMap.get( PASSIVE_STATE )[ 0 ] + "'>" );
		Writer.write( "<input type='radio' name='" + STATE_PARAM + "' value='" + PASSIVE_STATE + "'" + buttonMap.get( PASSIVE_STATE )[ 1 ] + ">Passive</input>" );
		Writer.write( "</label>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );

		Writer.write( "</div>" );
		Writer.write( "</form>" );
	}

	private void renderTable()
	{
		ListModel< PObjectModel > model = getModel();
		
		Writer.write( "<table class='table table-striped table-hover'>" );
		Writer.write( "<thead>" );
		Writer.write( "<tr>" );
		
		renderTableHeaderRow();
		
		Writer.write( "</tr>" );
		Writer.write( "</thead>" );

		Writer.write( "<tbody>" );
		
		for ( PObjectModel row : model )
		{
			String oid = Transformation.getString( PObjectOIDTransformer.class, row.getOid() );
			String detailPageLink = SaraswatiServlet.getPageURL( getDetailPageClass() ) + "?" + OID_PARAM + "=" + oid;
			
			Writer.write( "<tr class='clickableRow' style='cursor: pointer;' href='" + detailPageLink + "'>" );
			
			renderTableDataRow( row );
			
			Writer.write( "</tr>" );
		}
		
		Writer.write( "</tbody>" );
		
		Writer.write( "</table>" );
	}

	protected abstract void renderTableHeaderRow();
	protected abstract void renderTableDataRow( PObjectModel row );
	protected abstract Class< ? > getDetailPageClass();
}
