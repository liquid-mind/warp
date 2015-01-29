package ch.shaktipat.saraswati.internal.instrument.method;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.analysis.Frame;
import ch.shaktipat.exwrapper.javassist.bytecode.CodeIteratorWrapper;
import ch.shaktipat.saraswati.internal.common.DebugConstants;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;

public class InstrumentationHelper
{
	private static final Logger logger = Logger.getLogger( InstrumentationHelper.class.getName() );

	public static boolean methodIsSynchronized( MethodInfo methodInfo )
	{
		boolean methodIsSynchronized = false;

		if ( ( methodInfo.getAccessFlags() & AccessFlag.SYNCHRONIZED ) != 0 )
		{
			methodIsSynchronized = true;
		}

		return methodIsSynchronized;
	}

	public static boolean methodHasImplementation( MethodInfo methodInfo )
	{
		boolean methodHasImplementation = true;

		if ( methodInfo.getCodeAttribute() == null )
		{
			methodHasImplementation = false;
		}

		return methodHasImplementation;
	}

	public static void emitComment( Bytecode bytecode, ConstPool constPool, String comment )
	{
		if ( DebugConstants.CONTAINER_INSTRUMENT_BYTECODE_GENERATOR_DEBUG )
		{
			bytecode.addLdc( ConstPoolHelper.getStringInfo( constPool, "***COMMENT***: " + comment ) );
			bytecode.addOpcode( Opcode.POP );
		}
	}

	public static boolean isDeadCode( Frame frame )
	{
		// This condition occurs when the analyzer identifies dead code
		// (Note that I had this happen in a try/catch/finally where
		// the try/catch were empty and the finally called an
		// instrumentable method - for some reason the compiler
		// output the invocation twice: once properly and once in
		// unreachable code.)
		if ( frame == null )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static List< MethodInvocation > getMethodInvocations( MethodInfo methodInfo, ClassPool classPool, ConstPool constPool, Frame[] frames )
	{
		List< MethodInvocation > methodInvocations = new ArrayList< MethodInvocation >();

		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		CodeIterator codeIterator = codeAttribute.iterator();

		while ( codeIterator.hasNext() )
		{
			int index = CodeIteratorWrapper.next( codeIterator );
			int op = codeIterator.byteAt( index );

			if ( isInvocation( op ) )
			{
				int cpIndex = codeIterator.u16bitAt( index + 1 );
				MethodInvocationType type = getMethodInvocationType( op );
				int lineNumber = methodInfo.getLineNumber( index );
				String className = null;
				String methodName = null;
				String methodDescriptor = null;

				if ( type.equals( MethodInvocationType.INVOKE_INTERFACE ) )
				{
					className = constPool.getClassInfoByDescriptor( constPool.getInterfaceMethodrefClass( cpIndex ) );
					methodName = constPool.getInterfaceMethodrefName( cpIndex );
					methodDescriptor = constPool.getInterfaceMethodrefType( cpIndex );
				}
				else
				{
					className = constPool.getClassInfoByDescriptor( constPool.getMethodrefClass( cpIndex ) );
					methodName = constPool.getMethodrefName( cpIndex );
					methodDescriptor = constPool.getMethodrefType( cpIndex );
				}

				if ( isInstrumentedInvocation( frames, index, classPool, className, methodName, methodDescriptor ) )
				{
					methodInvocations.add( new MethodInvocation( index, new FQJVMMethodName( className, methodName, methodDescriptor ), lineNumber, type ) );
				}
			}
		}

		return methodInvocations;
	}

	private static boolean isInstrumentedInvocation( Frame[] frames, int index, ClassPool classPool, String className, String methodName, String type )
	{
		boolean isInstrumentedInvocation = true;

		if ( isDeadCode( frames[ index ] ) )
		{
			isInstrumentedInvocation = false;
			logger.fine( "Instrumentable method detected in dead code section: invoked class=" + className + ", invoked method=" + methodName + ", bytecode offset=" + index );
		}

		return isInstrumentedInvocation;
	}

	private static boolean isInvocation( int opcode )
	{
		boolean isInvocation = false;

		if ( ( opcode == Opcode.INVOKEVIRTUAL ) || ( opcode == Opcode.INVOKESPECIAL ) || ( opcode == Opcode.INVOKEINTERFACE ) || ( opcode == Opcode.INVOKESTATIC ) )
		{
			isInvocation = true;
		}

		return isInvocation;
	}

	private static MethodInvocationType getMethodInvocationType( int opcode )
	{
		MethodInvocationType type = null;

		switch ( opcode ) {
			case Opcode.INVOKEVIRTUAL:
				type = MethodInvocationType.INVOKE_VIRTUAL;
				break;
			case Opcode.INVOKESPECIAL:
				type = MethodInvocationType.INVOKE_SPECIAL;
				break;
			case Opcode.INVOKEINTERFACE:
				type = MethodInvocationType.INVOKE_INTERFACE;
				break;
			case Opcode.INVOKESTATIC:
				type = MethodInvocationType.INVOKE_STATIC;
				break;
			default:
				throw new IllegalStateException( "Invocation is of unexpected / unknown type." );
		}

		return type;
	}
}
