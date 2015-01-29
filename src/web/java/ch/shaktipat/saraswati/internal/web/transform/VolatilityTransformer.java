package ch.shaktipat.saraswati.internal.web.transform;

import java.util.HashMap;
import java.util.Map;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.volatility.Volatility;

@TransformerMarker
public class VolatilityTransformer implements Transformer< Volatility >
{
	private static final Map< Volatility, String > VOLATILITY_MAP;
	
	static
	{
		VOLATILITY_MAP = new HashMap< Volatility, String >();
		VOLATILITY_MAP.put( Volatility.DYNAMIC, "dynamic" );
		VOLATILITY_MAP.put( Volatility.STABLE, "stable" );
		VOLATILITY_MAP.put( Volatility.VOLATILE, "volatile" );
	}
	
	@Override
	public String getString( Volatility volatility )
	{
		return VOLATILITY_MAP.get( volatility );
	}

	@Override
	public Volatility getValue( String volatilityAsString )
	{
		throw new UnsupportedOperationException();
	}
}
