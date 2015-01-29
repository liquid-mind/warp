package ch.shaktipat.exwrapper.org.apache.commons.io.output;

import java.io.IOException;

import org.apache.commons.io.output.LockableFileWriter;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class LockableFileWriterWrapper
{
	public static LockableFileWriter __new( String fileName, boolean append, String lockDir ) 
	{
		try
		{
			return new LockableFileWriter( fileName, append, lockDir );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
