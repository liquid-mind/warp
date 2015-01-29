package ch.shaktipat.exwrapper.java.util.jar;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class JarOutputStreamWrapper
{
	public static JarOutputStream __new( OutputStream os )
	{
		try
		{
			return new JarOutputStream( os );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void putNextEntry( JarOutputStream obj, ZipEntry ze )
	{
		try
		{
			obj.putNextEntry( ze );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
