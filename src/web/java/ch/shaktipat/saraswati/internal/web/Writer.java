package ch.shaktipat.saraswati.internal.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.exwrapper.javax.servlet.http.HttpServletResponseWrapper;

public class Writer
{
	private static ThreadLocal< PrintWriter > writer = new ThreadLocal< PrintWriter >();
	private static ThreadLocal< Integer > indention = new ThreadLocal< Integer >();
	
	public static void setup( HttpServletResponse response)
	{
		writer.set( new PrintWriter( HttpServletResponseWrapper.getOutputStream( response ) ) );
		indention.set( -1 );
	}
	
	public static void teardown()
	{
		writer.get().close();
		writer.set( null );
		indention.set( null );
	}
	
	public static void write()
	{
		write( "" );
	}
	
	public static void write( String msg )
	{
		int indentionAdjust = ( msg.startsWith( "</" ) ? -1 : 0 );
		int currentIndention = indention.get();
		
		writer.get().println( StringUtils.repeat( "\t", currentIndention + indentionAdjust ) + msg );
		
		msg = msg.replaceAll( "\"[^\"]*\"", "" );
		msg = msg.replaceAll( "\'[^\']*\'", "" );
		
		int dec = StringUtils.countMatches( msg, "</" );
		int inc = StringUtils.countMatches( msg, "<" ) - dec;
		dec += StringUtils.countMatches( msg, "/>" );
		
		indention.set( currentIndention + inc - dec );
	}
	
	public static PrintWriter getWriter()
	{
		return writer.get();
	}
}
