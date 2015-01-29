package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

@TransformerMarker
public class PEventQueueOIDTransformer implements Transformer< PEventQueueOID >
{
	@Override
	public String getString( PEventQueueOID pEventQueueOID )
	{
		return pEventQueueOID.toString();
	}

	@Override
	public PEventQueueOID getValue( String pEventQueueOIDAsString )
	{
		return new PEventQueueOID( pEventQueueOIDAsString );
	}
}
