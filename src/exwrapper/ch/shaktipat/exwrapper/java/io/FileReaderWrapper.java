package ch.shaktipat.exwrapper.java.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class FileReaderWrapper
{
	public static FileReader __new( String fileName ) 
	{
		try
		{
			return new FileReader( fileName );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static FileReader __new( File file ) 
	{
		try
		{
			return new FileReader( file );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
