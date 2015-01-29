package ch.shaktipat.exwrapper.javassist.bytecode;

import ch.shaktipat.exwrapper.javassist.NotFoundExceptionWrapper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

public class DescriptorWrapper
{
	public static CtClass getReturnType( String descriptor, ClassPool cp ) throws NotFoundExceptionWrapper
	{
		try
		{
			return Descriptor.getReturnType( descriptor, cp );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
	
	public static CtClass[] getParameterTypes( String descriptor, ClassPool cp ) throws NotFoundExceptionWrapper
	{
		try
		{
			return Descriptor.getParameterTypes( descriptor, cp );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
	
	public static CtClass toCtClass( String descriptor, ClassPool cp ) throws NotFoundExceptionWrapper
	{
		try
		{
			return Descriptor.toCtClass( descriptor, cp );
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
}
