package ch.shaktipat.saraswati.internal.pool;

import java.io.IOException;
import java.io.InputStream;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;
import ch.shaktipat.saraswati.internal.common.ClassLoaderAwareObjectInputStream;

public class SaraswatiObjectInputStream extends ClassLoaderAwareObjectInputStream
{
	public static SaraswatiObjectInputStream create( InputStream is )
	{
		try
		{
			return new SaraswatiObjectInputStream( is );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public SaraswatiObjectInputStream( InputStream in ) throws IOException
	{
		super( in );
	}
}
