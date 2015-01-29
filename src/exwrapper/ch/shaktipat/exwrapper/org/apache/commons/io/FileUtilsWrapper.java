package ch.shaktipat.exwrapper.org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class FileUtilsWrapper
{
	public static void deleteDirectory( File directory ) 
	{
		try
		{
			FileUtils.deleteDirectory( directory );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void forceMkdir( File directory ) 
	{
		try
		{
			FileUtils.forceMkdir( directory );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static URL[] toURLs( File[] files ) 
	{
		try
		{
			return FileUtils.toURLs( files );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static byte[] readFileToByteArray( File file ) 
	{
		try
		{
			return FileUtils.readFileToByteArray( file );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void touch( File file ) 
	{
		try
		{
			FileUtils.touch( file );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
