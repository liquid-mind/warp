package ch.shaktipat.exwrapper.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;

public class CtFieldWrapper
{
	public static CtField __new( CtClass type, String name, CtClass declaring )
	{
		try
		{
			return new CtField( type, name, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
}
