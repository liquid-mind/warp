package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

@TransformerMarker
public class PObjectOIDTransformer implements Transformer< PObjectOID >
{
	@Override
	public String getString( PObjectOID pObjectOID )
	{
		return pObjectOID.toString();
	}

	@Override
	public PObjectOID getValue( String pObjectOIDjAsString )
	{
		throw new UnsupportedOperationException();
	}
}
