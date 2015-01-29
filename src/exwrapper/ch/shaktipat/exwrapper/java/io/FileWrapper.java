package ch.shaktipat.exwrapper.java.io;

import java.io.File;
import java.io.IOException;

public class FileWrapper
{
	public static File createTempFile( String prefix, String suffix, File directory )
	{
		try
		{
			return File.createTempFile( prefix, suffix, directory );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
