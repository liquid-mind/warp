package ch.shaktipat.saraswati.internal.web.controller.psharedobject;

import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.controller.POwnableObjectController;
import ch.shaktipat.saraswati.internal.web.transform.PSharedObjectOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.pobject.oid.PSharedObjectOID;

public abstract class PSharedObjectController extends POwnableObjectController
{
	@Override
	public PSharedObjectOID getPObjectOIDParameter()
	{
		String pSharedObjectOIDAsString = SaraswatiServletContext.getParameter( OID_PARAM );
		PSharedObjectOID pSharedObjectOID = Transformation.getValue( PSharedObjectOIDTransformer.class, pSharedObjectOIDAsString );
		
		return pSharedObjectOID;	
	}
}
