package ch.shaktipat.exwrapper.javassist;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.ClassNotFoundExceptionWrapper;

public class CtClassWrapper
{
	public static void writeFile( CtClass ctClass, String directoryName )
	{
		try
		{
			ctClass.writeFile( directoryName );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static byte[] toBytecode( CtClass ctClass )
	{
		try
		{
			return ctClass.toBytecode();
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}

	public static CtClass getSuperclass( CtClass ctClass )
	{
		try
		{
			return ctClass.getSuperclass();
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static CtMethod getDeclaredMethod( CtClass ctClass, String name, CtClass[] params )
	{
		try
		{
			return ctClass.getDeclaredMethod( name, params );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static CtConstructor getDeclaredConstructor( CtClass ctClass, String name, CtClass[] params )
	{
		try
		{
			return ctClass.getDeclaredConstructor( params );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static CtMethod getMethod( CtClass ctClass, String name, String desc )
	{
		try
		{
			return ctClass.getMethod( name, desc );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static void addMethod( CtClass ctClass, CtMethod m )
	{
		try
		{
			ctClass.addMethod( m );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}

	public static void addConstructor( CtClass ctClass, CtConstructor c )
	{
		try
		{
			ctClass.addConstructor( c );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}

	public static void removeConstructor( CtClass ctClass, CtConstructor c )
	{
		try
		{
			ctClass.removeConstructor( c );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}

	public static Object getAnnotation( CtClass ctClass, Class< ? > clz )
	{
		try
		{
			return ctClass.getAnnotation( clz );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}
	
	public static void addField( CtClass ctClass, CtField f )
	{
		try
		{
			ctClass.addField( f );
		}
		catch ( CannotCompileException e )
		{
			throw new CannotCompileExceptionWrapper( e );
		}
	}
	
	public static CtClass[] getInterfaces( CtClass ctClass )
	{
		try
		{
			return ctClass.getInterfaces();
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
}
