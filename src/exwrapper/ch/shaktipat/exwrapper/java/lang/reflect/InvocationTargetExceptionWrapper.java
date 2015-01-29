package ch.shaktipat.exwrapper.java.lang.reflect;

import java.lang.reflect.InvocationTargetException;

public class InvocationTargetExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InvocationTargetExceptionWrapper( InvocationTargetException e )
	{
		super( e );
	}
}
