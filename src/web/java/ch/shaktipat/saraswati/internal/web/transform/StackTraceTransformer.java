package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class StackTraceTransformer implements Transformer< StackTraceElement[] >
{

	@Override
	public String getString( StackTraceElement[] stackTraceElements )
	{
		String stackTraceAsString = null;
		
		if ( stackTraceElements != null && stackTraceElements.length > 0 )
		{
			stackTraceAsString = "";
			
			for ( StackTraceElement stackTraceElement : stackTraceElements )
				stackTraceAsString += stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() +
					"(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")";
		}
		
		return stackTraceAsString;
	}

	@Override
	public StackTraceElement[] getValue( String objAsString )
	{
		throw new UnsupportedOperationException();
	}
}
