package ch.shaktipat.saraswati.internal.instrument.method;

import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.Type;

// TODO: perhaps ought to introduce own frame class that does
// the things I need; might be more consistent. At the moment,
// pushing and popping is done sometimes one way and sometimes
// another...
public class FrameHelper
{
	public static void push( Frame frame, Type type )
	{
		frame.push( type );
		
		if ( type == Type.LONG || type == Type.DOUBLE )
			frame.push( Type.TOP );
	}
	
	public static Type pop( Frame frame )
	{
		Type type = frame.pop();
		
		if ( type == Type.TOP )
			type = frame.pop();
		
		return type;
	}
	
	public static String toString( Frame frame )
	{
		String frameAsString = frame.getClass().getName() + "@" + frame.hashCode() + System.lineSeparator();
		
		frameAsString += "{" + System.lineSeparator();
		
		// Registers
		frameAsString += "\tRegisters";

		frameAsString += "\t{" + System.lineSeparator();
		
		for ( int i = 0 ; i < frame.localsLength() ; ++i )
			frameAsString += "\t\t" + i + ": " + frame.getLocal( i ) + System.lineSeparator();
		
		frameAsString += "\t}" + System.lineSeparator();
				
		// Stack
		frameAsString += "\tStack elements";

		frameAsString += "\t{" + System.lineSeparator();
		
		for ( int i = 0 ; i < frame.getTopIndex() + 1 ; ++i )
			frameAsString += "\t\t" + i + ": " + frame.getStack( i ) + System.lineSeparator();
		
		frameAsString += "\t}" + System.lineSeparator();
				
		frameAsString += "}" + System.lineSeparator();
		
		return frameAsString;
	}

	public static String toString( Frame[] frames )
	{
		String framesAsString = "";
		
		for ( int i = 0 ; i < frames.length ; ++i )
		{
			Frame frame = frames[ i ];
			
			framesAsString += "Frame #" + i + System.lineSeparator();
			
			if ( frame != null )
				framesAsString += toString( frame );
		}
	
		return framesAsString;
	}
}
