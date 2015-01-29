package ch.shaktipat.exwrapper.java.rmi;

import java.rmi.NotBoundException;

public class NotBoundExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NotBoundExceptionWrapper( NotBoundException e )
	{
		super( e );
	}
}
