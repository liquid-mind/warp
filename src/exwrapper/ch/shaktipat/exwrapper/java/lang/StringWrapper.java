package ch.shaktipat.exwrapper.java.lang;

import java.io.UnsupportedEncodingException;

import ch.shaktipat.exwrapper.java.io.UnsupportedEncodingExceptionWrapper;

public class StringWrapper
{
	public static byte[] getBytes( String theString, String encoding )
	{
		try
		{
			return theString.getBytes( encoding );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new UnsupportedEncodingExceptionWrapper( e );
		}
	}

}
