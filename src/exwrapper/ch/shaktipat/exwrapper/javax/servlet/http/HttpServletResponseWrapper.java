package ch.shaktipat.exwrapper.javax.servlet.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class HttpServletResponseWrapper
{
	public static OutputStream getOutputStream( HttpServletResponse response ) 
	{
		try
		{
			return response.getOutputStream();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void sendError( HttpServletResponse response, int code ) 
	{
		try
		{
			response.sendError( code );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
