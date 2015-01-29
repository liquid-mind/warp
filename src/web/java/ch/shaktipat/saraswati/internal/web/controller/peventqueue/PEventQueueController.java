package ch.shaktipat.saraswati.internal.web.controller.peventqueue;

import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.controller.POwnableObjectController;
import ch.shaktipat.saraswati.internal.web.transform.PEventQueueOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public abstract class PEventQueueController extends POwnableObjectController
{
	@Override
	public PEventQueueOID getPObjectOIDParameter()
	{
		String pEventQueueOIDAsString = SaraswatiServletContext.getParameter( OID_PARAM );
		PEventQueueOID pEventQueueOID = Transformation.getValue( PEventQueueOIDTransformer.class, pEventQueueOIDAsString );
		
		return pEventQueueOID;	
	}
}
