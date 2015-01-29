package ch.shaktipat.exwrapper.javassist.bytecode;

import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;

public class MethodInfoWrapper
{
	public static void rebuildStackMap( MethodInfo obj, ClassPool pool )
	{
		try
		{
			obj.rebuildStackMap( pool );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}
}
