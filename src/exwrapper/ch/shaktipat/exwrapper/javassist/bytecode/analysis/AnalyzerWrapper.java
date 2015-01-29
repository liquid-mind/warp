package ch.shaktipat.exwrapper.javassist.bytecode.analysis;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import ch.shaktipat.exwrapper.javassist.bytecode.BadBytecodeWrapper;

public class AnalyzerWrapper
{
	public static Frame[] analyze( Analyzer analyzer, CtClass clazz, MethodInfo method )
	{
		try
		{
			return analyzer.analyze( clazz, method );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}
	
	public static Frame[] analyze( Analyzer analyzer, CtMethod ctMethod )
	{
		try
		{
			return analyzer.analyze( ctMethod );
		}
		catch ( BadBytecode e )
		{
			throw new BadBytecodeWrapper( e );
		}
	}
}
