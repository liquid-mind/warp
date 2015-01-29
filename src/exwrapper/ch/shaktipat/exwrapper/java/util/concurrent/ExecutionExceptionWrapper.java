package ch.shaktipat.exwrapper.java.util.concurrent;

import java.util.concurrent.ExecutionException;

public class ExecutionExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ExecutionExceptionWrapper( ExecutionException e )
	{
		super( e );
	}
}
