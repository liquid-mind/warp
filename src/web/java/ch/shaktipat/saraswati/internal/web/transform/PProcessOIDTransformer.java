package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

@TransformerMarker
public class PProcessOIDTransformer implements Transformer< PProcessOID >
{
	@Override
	public String getString( PProcessOID pProcessOID )
	{
		String pProcessOIDAsString = null;
		
		if ( pProcessOID != null )
			pProcessOIDAsString = pProcessOID.toString();
		
		return pProcessOIDAsString;
	}

	@Override
	public PProcessOID getValue( String pProcessOIDjAsString )
	{
		PProcessOID oid = null;
		
		if ( pProcessOIDjAsString != null && !pProcessOIDjAsString.isEmpty())
			oid = new PProcessOID( pProcessOIDjAsString );
		
		return oid;
	}
}
