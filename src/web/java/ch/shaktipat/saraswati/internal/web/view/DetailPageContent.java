package ch.shaktipat.saraswati.internal.web.view;

import java.util.Collection;

import ch.shaktipat.saraswati.internal.pool.PersistentObjectNotFoundExeception;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;

public abstract class DetailPageContent extends Content
{
	public DetailPageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		try
		{
			if ( getController().getPObjectOIDParameter() == null )
				renderFailure();
			else
				renderSuccess();
		}
		catch ( PersistentObjectNotFoundExeception e )
		{
			renderFailure();
		}
	}

	private void renderFailure()
	{
		Writer.write( "<div class='row'>" );
		Writer.write( "<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12'>" );
		Writer.write( "<div class='alert alert-danger'>No object with OID " + getController().getPObjectOIDParameter() + " found.</div>" );
		Writer.write( "</div>" );
		Writer.write( "</div>" );
	}
	
	protected abstract void renderSuccess();
	
	protected void renderDetail( View titleView, View detailView )
	{
		Writer.write( "<div class='row'>" );
		Writer.write( "<div class='col-lg-2 col-md-2 col-sm-3 col-xs-3'>" );
		
		titleView.render();
		
		Writer.write( "</div>" );
		Writer.write( "<div class='col-lg-10 col-md-10 col-sm-9 col-xs-9'>" );
		
		detailView.render();
		
		Writer.write( "</div>" );
		Writer.write( "</div>" );
	}
	
	protected void renderDetail( final String title, final String value )
	{
		if ( value == null )
			return;
		
		renderDetail( new View( getController() ) {
			@Override public void render() { Writer.write( title ); }
		}, new View( getController() ) {
			@Override public void render() { Writer.write( value ); }
		} );
	}
	
	protected void renderDetails( final String title, final Collection< String > values )
	{
		if ( values == null )
			return;
		
		renderDetail( new View( getController() ) {
			@Override public void render() { Writer.write( title ); }
		}, new View( getController() ) {
			@Override public void render() {
				for ( String value : values )
					Writer.write( value + "<br>");
		} } );
	}
	
	protected void renderDetailWithLink( final String title, final String value, final String link )
	{
		if ( value == null )
			return;
		
		renderDetail( new View( getController() ) {
			@Override public void render() { Writer.write( title ); }
		}, new View( getController() ) {
			@Override public void render() { Writer.write( "<a href='" + link + "'>" + value + "</a>" ); }
		} );
	}
	
	protected void renderExpandableDetail( final String title, final String value )
	{
		if ( value == null )
			return;
		
		final String[] lines = value.split( "\\r?\\n" );
		final String expandableID = getElementID();
		
		renderDetail( new View( getController() ) {
			@Override public void render()
			{
				Writer.write( title + " <a data-toggle='collapse' href='#" + expandableID + "'>[+|-]</a>" );
			}
		}, new View( getController() ) {
			@Override public void render()
			{
				Writer.write( "<div class='panel panel-default'>" );
				Writer.write( "<div class='panel-body' style='overflow: scroll;'>" );
				Writer.getWriter().print( "<div style='white-space: pre;'>" );
				
				for ( int i = 0 ; i < lines.length && i < 4 ; ++i )
					Writer.getWriter().println( lines[ i ] );
					
				Writer.getWriter().print( "</div>" );
				Writer.getWriter().print( "<div id='" + expandableID + "' class='collapse' style='white-space: pre;'>" );
				
				for ( int i = 4 ; i < lines.length ; ++i )
					Writer.getWriter().println( lines[ i ] );
					
				Writer.getWriter().print(  "</div>" );
				Writer.write(  "</div>" );
				Writer.write(  "</div>" );
			}
		} );
	}
}
