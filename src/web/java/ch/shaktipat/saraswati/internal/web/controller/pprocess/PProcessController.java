package ch.shaktipat.saraswati.internal.web.controller.pprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.shaktipat.saraswati.internal.web.SaraswatiServletContext;
import ch.shaktipat.saraswati.internal.web.WebConstants;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.transform.PProcessOIDTransformer;
import ch.shaktipat.saraswati.internal.web.transform.Transformation;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public abstract class PProcessController extends Controller
{
	@Override
	public PProcessOID getPObjectOIDParameter()
	{
		String pPProcessOIDAsString = SaraswatiServletContext.getParameter( OID_PARAM );
		PProcessOID pProcessOID = Transformation.getValue( PProcessOIDTransformer.class, pPProcessOIDAsString );
		
		return pProcessOID;	
	}

	protected String getState( PProcessHandle handle )
	{
		List< String > executionStates = new ArrayList< String >( Arrays.asList( handle.getLeafStates() ) );
		executionStates.removeAll( WebConstants.VOLATILITIES );
		
		return executionStates.iterator().next();
	}

	protected String getVolatility( PProcessHandle handle )
	{
		List< String > volatilityStates = new ArrayList< String >( Arrays.asList( handle.getLeafStates() ) );
		volatilityStates.retainAll( WebConstants.VOLATILITIES );
		
		return volatilityStates.iterator().next();
	}
}
