package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

@TransformerMarker
public class PSharedObjectOIDTransformer implements Transformer< PSharedObjectOID >
{
	@Override
	public String getString( PSharedObjectOID pSharedObjectOID )
	{
		return pSharedObjectOID.toString();
	}

	@Override
	public PSharedObjectOID getValue( String pSharedObjectOIDAsString )
	{
		return new PSharedObjectOID( pSharedObjectOIDAsString );
	}
}
