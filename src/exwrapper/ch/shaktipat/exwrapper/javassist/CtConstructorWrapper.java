package ch.shaktipat.exwrapper.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

public class CtConstructorWrapper
{
	public static void insertBefore( CtConstructor src, String sourceCode )
	{
		try
		{
			src.insertBefore( sourceCode );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtClass[] getParameterTypes( CtConstructor src )
	{
		try
		{
			return src.getParameterTypes();
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
	
	public static void addParameter( CtConstructor src, CtClass paramType )
	{
		try
		{
			src.addParameter( paramType );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtMethod toMethod( CtConstructor src, String name, CtClass declaring )
	{
		try
		{
			return src.toMethod( name, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
}
