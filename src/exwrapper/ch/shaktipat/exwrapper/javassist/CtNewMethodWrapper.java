package ch.shaktipat.exwrapper.javassist;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

public class CtNewMethodWrapper
{
	public static CtMethod copy( CtMethod src, String name, CtClass declaring, ClassMap map)
	{
		try
		{
			return CtNewMethod.copy( src, name, declaring, map );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtMethod make( CtClass returnType, java.lang.String mname, CtClass[] parameters, CtClass[] exceptions, java.lang.String body, CtClass declaring )
	{
		try
		{
			return CtNewMethod.make( returnType, mname, parameters, exceptions, body, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
}
