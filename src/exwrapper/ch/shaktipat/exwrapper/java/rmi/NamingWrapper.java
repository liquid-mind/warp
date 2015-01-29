package ch.shaktipat.exwrapper.java.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import ch.shaktipat.exwrapper.java.net.MalformedURLExceptionWrapper;

public class NamingWrapper
{
	public static Object lookup( String name )
	{
		try
		{
			return Naming.lookup( name );
		}
		catch ( RemoteException e )
		{
			throw new RemoteExceptionWrapper( e );
		}
		catch ( MalformedURLException e )
		{
			throw new MalformedURLExceptionWrapper( e );
		}
		catch ( NotBoundException e )
		{
			throw new NotBoundExceptionWrapper( e );
		}
	}
}
