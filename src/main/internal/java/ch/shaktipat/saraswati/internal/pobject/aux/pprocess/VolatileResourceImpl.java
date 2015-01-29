package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl.VolatilityChangeType;
import ch.shaktipat.saraswati.volatility.VolatileResource;

public class VolatileResourceImpl implements VolatileResource
{
	private static final long serialVersionUID = 1L;

	private boolean isEnlisted = false;
	private PersistentProcess pProcess;
	
	public VolatileResourceImpl( PersistentProcess pProcess )
	{
		super();
		this.pProcess = pProcess;
	}

	@Override
	public void enlist()
	{
		if ( isEnlisted )
			throw new RuntimeException( "Volatile resource is already enlisted." );
		
		isEnlisted = true;
		
		try
		{
			pProcess.getSelfProxy().notifyVolatilityChange( VolatilityChangeType.ENLIST_RESOURCE );
		}
		catch ( Throwable t )
		{
			isEnlisted = false;
			throw t;
		}
	}

	@Override	
	public void deEnlist()
	{
		if ( !isEnlisted )
			throw new RuntimeException( "Volatile resource is not enlisted." );
		
		isEnlisted = false;
		
		pProcess.getSelfProxy().notifyVolatilityChange( VolatilityChangeType.DEENLIST_RESOURCE );
	}

	@Override
	public boolean isEnlisted()
	{
		return isEnlisted;
	}
}
