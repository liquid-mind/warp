package ch.shaktipat.exwrapper.javassist;

import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;

public class CtNewConstructorWrapper
{
	public static CtConstructor copy( CtConstructor src, CtClass declaring, ClassMap map )
	{
		try
		{
			return CtNewConstructor.copy( src, declaring, map );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtConstructor defaultConstructor( CtClass ctClass )
	{
		try
		{
			return CtNewConstructor.defaultConstructor( ctClass );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtConstructor make( CtClass[] parameters, CtClass[] exceptions, CtClass declaring )
	{
		try
		{
			return CtNewConstructor.make( parameters, exceptions, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtConstructor make( CtClass[] parameters, CtClass[] exceptions, int howto, CtMethod body, CtMethod.ConstParameter cparam, CtClass declaring )
	{
		try
		{
			return CtNewConstructor.make( parameters, exceptions, howto, body, cparam, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
}
