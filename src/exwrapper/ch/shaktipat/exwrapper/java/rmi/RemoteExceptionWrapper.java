package ch.shaktipat.exwrapper.java.rmi;

import java.rmi.RemoteException;

public class RemoteExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public RemoteExceptionWrapper( RemoteException e )
	{
		super( e );
	}
}
