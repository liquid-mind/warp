package ch.shaktipat.saraswati.internal.web.controller.peventtopic;

import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.controller.POwnableObjectController;
import ch.shaktipat.saraswati.internal.web.transform.PEventTopicOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public abstract class PEventTopicController extends POwnableObjectController
{
	@Override
	public PEventTopicOID getPObjectOIDParameter()
	{
		String pEventTopicOIDAsString = SaraswatiServletContext.getParameter( OID_PARAM );
		PEventTopicOID pEventTopicOID = Transformation.getValue( PEventTopicOIDTransformer.class, pEventTopicOIDAsString );
		
		return pEventTopicOID;	
	}
}
