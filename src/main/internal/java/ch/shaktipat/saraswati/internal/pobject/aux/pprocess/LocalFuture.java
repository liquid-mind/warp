package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.pobject.PProcessHandle;

public class LocalFuture< T > extends PersistentProcessFuture< T >
{
	private static final long serialVersionUID = 1L;

	public LocalFuture( PProcessHandle pProcessHandle )
	{
		super( pProcessHandle );
	}

	@Override
	protected PersistentProcess getPersistentProcess()
	{
		return PersistentProcessImpl.getOtherProxy( getPProcessHandle().getOID() );
	}
}
