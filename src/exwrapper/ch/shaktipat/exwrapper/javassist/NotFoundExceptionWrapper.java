package ch.shaktipat.exwrapper.javassist;

import javassist.NotFoundException;

public class NotFoundExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public NotFoundExceptionWrapper( NotFoundException e )
	{
		super( e );
	}
}
