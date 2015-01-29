package ch.shaktipat.exwrapper.javassist.bytecode;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;

public class CodeAttributeWrapper
{
	public static int computeMaxStack( CodeAttribute codeAttribute )
	{
		try
		{
			return codeAttribute.computeMaxStack();
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}

	public static void insertLocalVar( CodeAttribute codeAttribute, int where, int size )
	{
		try
		{
			codeAttribute.insertLocalVar( where, size );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}
}
