package ch.shaktipat.exwrapper.java.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressWrapper
{
	public static InetAddress getByName( String host )
	{
		try
		{
			return InetAddress.getByName( host );
		}
		catch ( UnknownHostException e )
		{
			throw new UnknownHostExceptionWrapper( e );
		}
	}
}
