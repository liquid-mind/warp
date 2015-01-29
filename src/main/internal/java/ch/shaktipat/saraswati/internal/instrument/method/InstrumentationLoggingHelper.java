package ch.shaktipat.saraswati.internal.instrument.method;

import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CtClass;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.Type;
import ch.shaktipat.exwrapper.javassist.bytecode.CodeIteratorWrapper;
import ch.shaktipat.exwrapper.javassist.bytecode.analysis.AnalyzerWrapper;

public class InstrumentationLoggingHelper
{
	public static void outputMethod( Logger logger, MethodInfo methodInfo )
	{
		if ( logger.isLoggable( Level.FINE ) )
		{
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
			CodeIterator codeIterator = codeAttribute.iterator();

			codeIterator.begin();

			while ( codeIterator.hasNext() )
			{
				int index = CodeIteratorWrapper.next( codeIterator );
				int opcode = codeIterator.byteAt( index );
				String mnemonic = Mnemonic.OPCODE[ opcode ];
				logger.fine( index + ": " + mnemonic );
			}
		}
	}

	public static void outputStackFrames( Logger logger, CtClass ctClass, MethodInfo methodInfo )
	{
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		CodeIterator codeIterator = codeAttribute.iterator();

		// output stack frames
		Analyzer analyzer = new Analyzer();
		Frame[] frames = AnalyzerWrapper.analyze( analyzer, ctClass, methodInfo );

		codeIterator.begin();
		while ( codeIterator.hasNext() )
		{
			int index = CodeIteratorWrapper.next( codeIterator );
			Frame currentFrame = frames[ index ];

			// Skip frames in dead code sections
			if ( InstrumentationHelper.isDeadCode( currentFrame ) )
			{
				continue;
			}

			int stackSize = currentFrame.getTopIndex() + 1;
			int localsSize = currentFrame.localsLength();

			logger.fine( index + ": stack size: " + stackSize + ", locals size: " + localsSize );

			String stackTypes = "stack types: ";
			for ( int i = 0; i < stackSize; ++i )
			{
				Type type = currentFrame.getStack( i );
				stackTypes += type;

				if ( ( i + 1 ) < stackSize )
				{
					stackTypes += ", ";
				}
			}
			logger.fine( stackTypes );

			String localVarTypes = "local variable types: ";
			for ( int i = 0; i < localsSize; ++i )
			{
				Type type = currentFrame.getLocal( i );
				localVarTypes += type;

				if ( ( i + 1 ) < localsSize )
				{
					localVarTypes += ", ";
				}
			}
			logger.fine( localVarTypes );
		}
	}

}
