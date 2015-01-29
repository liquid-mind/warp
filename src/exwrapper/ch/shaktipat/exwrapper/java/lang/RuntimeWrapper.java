package ch.shaktipat.exwrapper.java.lang;

import java.io.IOException;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class RuntimeWrapper
{
	public static Process exec( Runtime runtime, String command, String[] envp )
	{
		try
		{
			return runtime.exec( command, envp );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
