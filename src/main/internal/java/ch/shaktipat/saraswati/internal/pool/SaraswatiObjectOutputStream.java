package ch.shaktipat.saraswati.internal.pool;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class SaraswatiObjectOutputStream extends ObjectOutputStream
{
	public static SaraswatiObjectOutputStream create( OutputStream os )
	{
		try
		{
			return new SaraswatiObjectOutputStream( os );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public SaraswatiObjectOutputStream( OutputStream out ) throws IOException
	{
		super( out );
	}
}
