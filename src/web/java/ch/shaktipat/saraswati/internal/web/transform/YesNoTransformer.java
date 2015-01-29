package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;


@TransformerMarker
public class YesNoTransformer implements Transformer< Boolean >
{
	public static final String YES = "Yes";
	public static final String NO = "No";
	
	@Override
	public String getString( Boolean booleanValue )
	{
		return ( booleanValue == true ? YES : NO );
	}

	@Override
	public Boolean getValue( String booleanAsString )
	{
		return ( booleanAsString.equals( YES ) ? true : false );
	}
}
