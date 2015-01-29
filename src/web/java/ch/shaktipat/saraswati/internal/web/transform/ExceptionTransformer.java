package ch.shaktipat.saraswati.internal.web.transform;

import java.io.PrintWriter;
import java.io.StringWriter;

import ch.shaktipat.exwrapper.java.io.WriterWrapper;
import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class ExceptionTransformer implements Transformer< Throwable >
{
	@Override
	public String getString( Throwable throwable )
	{
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace( new PrintWriter( stringWriter ) );
		String stackTrace = stringWriter.toString();
		WriterWrapper.close( stringWriter );
		
		return stackTrace;
	}

	@Override
	public Throwable getValue( String pProcessOIDjAsString )
	{
		throw new UnsupportedOperationException();
	}
}
