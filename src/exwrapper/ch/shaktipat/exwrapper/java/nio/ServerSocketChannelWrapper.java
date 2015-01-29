package ch.shaktipat.exwrapper.java.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class ServerSocketChannelWrapper
{
	public static ServerSocketChannel open()
	{
		try
		{
			return ServerSocketChannel.open();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static SelectableChannel configureBlocking( ServerSocketChannel serverSocketChannel, boolean block )
	{
		try
		{
			return serverSocketChannel.configureBlocking( block );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static SelectionKey register( ServerSocketChannel serverSocketChannel, Selector sel, int ops )
	{
		try
		{
			return serverSocketChannel.register( sel, ops );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static SocketChannel accept( ServerSocketChannel serverSocketChannel )
	{
		try
		{
			return serverSocketChannel.accept();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static void close( ServerSocketChannel serverSocketChannel )
	{
		try
		{
			serverSocketChannel.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
