package ch.shaktipat.exwrapper.java.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class SocketChannelWrapper
{
	public static SelectableChannel configureBlocking( SocketChannel socketChannel, boolean block )
	{
		try
		{
			return socketChannel.configureBlocking( block );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static SelectionKey register( SocketChannel socketChannel, Selector sel, int ops )
	{
		try
		{
			return socketChannel.register( sel, ops );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static SocketChannel open()
	{
		try
		{
			return SocketChannel.open();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static boolean connect( SocketChannel socketChannel, SocketAddress remote )
	{
		try
		{
			return socketChannel.connect( remote );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void close( SocketChannel socketChannel )
	{
		try
		{
			socketChannel.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
