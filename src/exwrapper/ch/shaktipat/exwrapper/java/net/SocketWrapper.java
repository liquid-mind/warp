package ch.shaktipat.exwrapper.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;

public class SocketWrapper
{
	public static InputStream getInputStream( Socket socket )
	{
		try
		{
			return socket.getInputStream();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static OutputStream getOutputStream( Socket socket )
	{
		try
		{
			return socket.getOutputStream();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void connect( Socket socket, SocketAddress endpoint )
	{
		try
		{
			socket.connect( endpoint );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
	
	public static void close( Socket socket )
	{
		try
		{
			socket.close();
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
