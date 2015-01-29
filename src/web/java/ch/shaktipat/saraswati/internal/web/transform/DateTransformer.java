package ch.shaktipat.saraswati.internal.web.transform;

import java.util.Date;

import ch.shaktipat.exwrapper.java.text.DateFormatWrapper;
import ch.shaktipat.saraswati.internal.web.WebConstants;
import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class DateTransformer implements Transformer< Date >
{
	@Override
	public String getString( Date date )
	{
		String dateAsString = null;
		
		if ( date != null )
			dateAsString = WebConstants.STANDARD_DATE_FORMAT.format( date );
		
		return dateAsString;
	}

	@Override
	public Date getValue( String pProcessOIDjAsString )
	{
		return DateFormatWrapper.parse( WebConstants.STANDARD_DATE_FORMAT, pProcessOIDjAsString );
	}
}
