package ch.shaktipat.exwrapper.org.apache.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class IOUtilsWrapper
{
	public static void copy( InputStream input, OutputStream output ) 
	{
		try
		{
			IOUtils.copy( input, output );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
