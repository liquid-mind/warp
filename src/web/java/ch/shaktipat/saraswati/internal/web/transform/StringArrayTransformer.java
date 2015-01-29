package ch.shaktipat.saraswati.internal.web.transform;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class StringArrayTransformer implements Transformer< String[] >
{
	@Override
	public String getString( String[] strings )
	{
		return StringUtils.join( strings, ", " );
	}

	@Override
	public String[] getValue( String commaSeparatedStrings )
	{
		throw new UnsupportedOperationException();
	}
}
