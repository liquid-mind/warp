package ch.shaktipat.exwrapper.javassist;

import ch.shaktipat.exwrapper.java.lang.ClassNotFoundExceptionWrapper;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

public class CtBehaviorWrapper
{
	public static CtClass[] getParameterTypes( CtBehavior ctBehavior )
	{
		try
		{
			return ctBehavior.getParameterTypes();
		}
		catch ( NotFoundException e )
		{
			throw new NotFoundExceptionWrapper( e );
		}
	}
	
	public static Object getAnnotation( CtBehavior ctBehavior, Class< ? > theClass )
	{
		try
		{
			return ctBehavior.getAnnotation( theClass );
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassNotFoundExceptionWrapper( e );
		}
	}
}
