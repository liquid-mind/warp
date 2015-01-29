package ch.shaktipat.saraswati.internal.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.javax.servlet.http.HttpServletResponseWrapper;
import ch.shaktipat.exwrapper.org.apache.commons.io.IOUtilsWrapper;
import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.view.View;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessListPage;

public class SaraswatiServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static final Set< String > STATIC_RESOURCE_POSTFIXES;
	
	static
	{
		STATIC_RESOURCE_POSTFIXES = new HashSet< String >();
		STATIC_RESOURCE_POSTFIXES.add( ".css" );
		STATIC_RESOURCE_POSTFIXES.add( ".png" );
		STATIC_RESOURCE_POSTFIXES.add( ".js" );
		STATIC_RESOURCE_POSTFIXES.add( ".map" );
		STATIC_RESOURCE_POSTFIXES.add( ".eot" );
		STATIC_RESOURCE_POSTFIXES.add( ".svg" );
		STATIC_RESOURCE_POSTFIXES.add( ".ttf" );
		STATIC_RESOURCE_POSTFIXES.add( ".woff" );
	}

	private static Map< String, Class< ? > > saraswatiPages;
	
	@Override
	public void init( ServletConfig config ) throws ServletException
	{
		super.init( config );

		getSaraswatiPages();
	}
	
	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		String pathInfo = request.getPathInfo();
		
		if ( pathInfo.equals( "/" ) )
			response.sendRedirect( "/" + PProcessListPage.class.getSimpleName() );
		else if ( isStaticResource( pathInfo ) )
			handleStaticResource( request, response );
		else
			handleDynamicResource( request, response );
	}
	
	private boolean isStaticResource( String pathInfo )
	{
		boolean isStaticResource = false;
		
		for ( String staticResourcePostfix : STATIC_RESOURCE_POSTFIXES )
		{
			if ( pathInfo.endsWith( staticResourcePostfix ) )
			{
				isStaticResource = true;
				break;
			}
		}

		return isStaticResource;
	}
	
	private void handleStaticResource( HttpServletRequest request, HttpServletResponse response ) throws ServletException
	{
		InputStream inputStream = SaraswatiServlet.class.getResourceAsStream( request.getPathInfo() );
		
		if ( inputStream == null )
		{
			HttpServletResponseWrapper.sendError( response, HttpServletResponse.SC_NOT_FOUND );
			return;
		}
		
		IOUtilsWrapper.copy( inputStream, HttpServletResponseWrapper.getOutputStream( response ) );
	}
	
	private void handleDynamicResource( HttpServletRequest request, HttpServletResponse response ) throws ServletException
	{
		Class< ? > targetClass = getSaraswatiPages().get( request.getPathInfo() );
		
		if ( targetClass == null )
		{
			HttpServletResponseWrapper.sendError( response, HttpServletResponse.SC_NOT_FOUND );
			return;
		}
		
		Writer.setup( response );
		SaraswatiServletContext.setup( request, response );
		
		try
		{
			View view = (View)ClassWrapper.newInstance( targetClass );
			view.render();
		}
		catch ( Throwable t )
		{
			Writer.write( "Error handling request:" );
			Writer.write( "<pre>" );
			t.printStackTrace( Writer.getWriter() );
			Writer.write( "</pre>" );
		}
		finally
		{
			SaraswatiServletContext.teardown();
			Writer.teardown();
		}
	}
	
	private Map< String, Class< ? > > getSaraswatiPages()
	{
		if ( saraswatiPages == null )
		{
			saraswatiPages = new HashMap< String, Class< ? > >();
			
			Reflections reflections = new Reflections( "" );
			Set< Class< ? > > saraswatiServices = reflections.getTypesAnnotatedWith( SaraswatiPage.class );
			
			for ( Class< ? > saraswatiService : saraswatiServices )
				saraswatiPages.put( "/" + saraswatiService.getSimpleName(), saraswatiService );
		}
		
		return saraswatiPages;
	}
	
	public static String getPageURL( Class< ? > pageClass )
	{
		return "/" + pageClass.getSimpleName();
	}
}
