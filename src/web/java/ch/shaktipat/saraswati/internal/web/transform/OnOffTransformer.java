package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;


@TransformerMarker
public class OnOffTransformer implements Transformer< Boolean >
{
	public static final String ON = "On";
	public static final String OFF = "Off";
	
	@Override
	public String getString( Boolean booleanValue )
	{
		return ( booleanValue == true ? ON : OFF );
	}

	@Override
	public Boolean getValue( String booleanAsString )
	{
		return ( booleanAsString.equals( ON ) ? true : false );
	}
}
