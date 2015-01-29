package ch.shaktipat.exwrapper.javassist;

import javassist.CannotCompileException;

public class CannotCompileExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public CannotCompileExceptionWrapper( CannotCompileException e )
	{
		super( e );
	}
}
