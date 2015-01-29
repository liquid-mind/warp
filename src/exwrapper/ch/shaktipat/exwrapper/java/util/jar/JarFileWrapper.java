package ch.shaktipat.exwrapper.java.util.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class JarFileWrapper
{
	public static JarFile __new( File file )
	{
		try
		{
			return new JarFile( file );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static JarFile __new( String file )
	{
		try
		{
			return new JarFile( file );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static InputStream getInputStream( JarFile jarFile, ZipEntry ze )
	{
		try
		{
			return jarFile.getInputStream( ze );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
