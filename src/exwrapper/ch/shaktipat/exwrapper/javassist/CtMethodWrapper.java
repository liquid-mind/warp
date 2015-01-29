package ch.shaktipat.exwrapper.javassist;

import ch.shaktipat.exwrapper.java.lang.ClassNotFoundExceptionWrapper;
import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.MethodInfo;

public class CtMethodWrapper
{
	public static CtMethod __new( CtMethod src, CtClass declaring, ClassMap map )
	{
		try
		{
			return new CtMethod( src, declaring, map );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}

	public static CtMethod make( MethodInfo minfo, CtClass declaring )
	{
		try
		{
			return CtMethod.make( minfo, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}

	public static CtMethod make( String src, CtClass declaring )
	{
		try
		{
			return CtMethod.make( src, declaring );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}

	public static Object getAnnotation( CtMethod method, Class< ? > clz )
	{
		try
		{
			return method.getAnnotation( clz );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}

	public static CtClass[] getParameterTypes( CtMethod method )
	{
		try
		{
			return method.getParameterTypes();
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
}
