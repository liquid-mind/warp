package ch.shaktipat.exwrapper.javassist.bytecode;

import javassist.bytecode.BadBytecode;

public class BadBytecodeWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public BadBytecodeWrapper( BadBytecode e )
	{
		super( e );
	}
}
