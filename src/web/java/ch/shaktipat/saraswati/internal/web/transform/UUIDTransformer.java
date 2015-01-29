package ch.shaktipat.saraswati.internal.web.transform;

import java.util.UUID;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class UUIDTransformer implements Transformer< UUID >
{
	@Override
	public String getString( UUID uuid )
	{
		return uuid.toString();
	}

	@Override
	public UUID getValue( String uuidAsString )
	{
		throw new UnsupportedOperationException();
	}
}
