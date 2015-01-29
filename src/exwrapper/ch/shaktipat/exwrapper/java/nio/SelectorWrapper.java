package ch.shaktipat.exwrapper.java.nio;

import java.io.IOException;
import java.nio.channels.Selector;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class SelectorWrapper
{
	public static Selector open()
	{
		try
		{
			return Selector.open();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static int select( Selector selector )
	{
		try
		{
			return selector.select();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void close( Selector selector )
	{
		try
		{
			selector.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
