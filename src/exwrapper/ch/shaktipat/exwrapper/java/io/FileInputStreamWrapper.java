package ch.shaktipat.exwrapper.java.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileInputStreamWrapper
{
	public static FileInputStream __new( File name )
	{
		try
		{
			return new FileInputStream( name );
		}
		catch ( FileNotFoundException e )
		{
			throw new FileNotFoundExceptionWrapper( e );
		}
	}
}
