package ch.shaktipat.exwrapper.javassist;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ClassPoolWrapper
{
	public static CtClass get( ClassPool classPool, String classname )
	{
		try
		{
			return classPool.get( classname );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static ClassPath insertClassPath( ClassPool classPool, String pathname )
	{
		try
		{
			return classPool.insertClassPath( pathname );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
}
