package ch.shaktipat.exwrapper.java.text;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateFormatWrapper
{
	public static Date parse( DateFormat dateFormat, String source )
	{
		try
		{
			return dateFormat.parse( source );
		}
		catch ( ParseException e )
		{
			throw new ParseExceptionWrapper( e );
		}
	}
}
