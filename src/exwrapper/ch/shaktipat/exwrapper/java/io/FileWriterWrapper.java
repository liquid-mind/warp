package ch.shaktipat.exwrapper.java.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class FileWriterWrapper
{
	public static FileWriter __new( String fileName, boolean append ) 
	{
		try
		{
			return new FileWriter( fileName, append );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static FileWriter __new( File file, boolean append ) 
	{
		try
		{
			return new FileWriter( file, append );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
