package ch.shaktipat.exwrapper.java.text;

import java.text.ParseException;

public class ParseExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ParseExceptionWrapper( ParseException e )
	{
		super( e );
	}
}
