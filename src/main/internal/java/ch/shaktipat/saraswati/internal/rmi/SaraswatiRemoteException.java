package ch.shaktipat.saraswati.internal.rmi;

import java.rmi.RemoteException;

public class SaraswatiRemoteException extends RemoteException
{
	private static final long serialVersionUID = 1L;

	public SaraswatiRemoteException( Throwable targetException )
	{
		super( "", targetException );
	}
}
