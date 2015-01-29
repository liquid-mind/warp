package ch.shaktipat.exwrapper.java.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class ServerSocketWrapper
{
	public static ServerSocket __new()
	{
		try
		{
			return new ServerSocket();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static ServerSocket __new( int port, int backlog, InetAddress bindAddr )
	{
		try
		{
			return new ServerSocket( port, backlog, bindAddr );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void bind( ServerSocket serverSocket, SocketAddress endpoint)
	{
		try
		{
			serverSocket.bind( endpoint );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void close( ServerSocket serverSocket )
	{
		try
		{
			serverSocket.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
