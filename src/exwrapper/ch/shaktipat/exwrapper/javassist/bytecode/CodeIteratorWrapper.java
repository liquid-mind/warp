package ch.shaktipat.exwrapper.javassist.bytecode;

import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;

public class CodeIteratorWrapper
{
	public static void insert( CodeIterator codeIterator, int pos, byte[] code )
	{
		try
		{
			codeIterator.insert( pos, code );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}

	public static void insertEx( CodeIterator codeIterator, int pos, byte[] code )
	{
		try
		{
			codeIterator.insertEx( pos, code );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}

	public static int next( CodeIterator codeIterator )
	{
		try
		{
			return codeIterator.next();
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}
}
