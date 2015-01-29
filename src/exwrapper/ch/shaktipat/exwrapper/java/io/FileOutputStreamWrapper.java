package ch.shaktipat.exwrapper.java.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileOutputStreamWrapper
{
	public static FileOutputStream __new( File name )
	{
		try
		{
			return new FileOutputStream( name );
		}
		catch ( FileNotFoundException e )
		{
			throw new FileNotFoundExceptionWrapper( e );
		}
	}
}
