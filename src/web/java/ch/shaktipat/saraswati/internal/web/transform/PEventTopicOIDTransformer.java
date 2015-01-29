package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

@TransformerMarker
public class PEventTopicOIDTransformer implements Transformer< PEventTopicOID >
{
	@Override
	public String getString( PEventTopicOID pEventTopicOID )
	{
		return pEventTopicOID.toString();
	}

	@Override
	public PEventTopicOID getValue( String pEventTopicOIDAsString )
	{
		return new PEventTopicOID( pEventTopicOIDAsString );
	}
}
