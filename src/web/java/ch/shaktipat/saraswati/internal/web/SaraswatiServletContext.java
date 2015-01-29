package ch.shaktipat.saraswati.internal.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SaraswatiServletContext
{
	private static ThreadLocal< HttpServletRequest > currentRequest = new ThreadLocal< HttpServletRequest >();
	private static ThreadLocal< HttpServletResponse > currentResponse = new ThreadLocal< HttpServletResponse >();
	
	public static void setup( HttpServletRequest request, HttpServletResponse response )
	{
		currentRequest.set( request );
		currentResponse.set( response );
	}
	
	public static void teardown()
	{
		currentRequest.set( null );
		currentResponse.set( null );
	}

	public static HttpServletRequest getRequest()
	{
		return currentRequest.get();
	}

	public static HttpServletResponse getResponse()
	{
		return currentResponse.get();
	}
	
	public static String getParameter( String name )
	{
		String param = getRequest().getParameter( name );
		
		if ( param == null )
			param = "";
		
		return param;
	}
}
