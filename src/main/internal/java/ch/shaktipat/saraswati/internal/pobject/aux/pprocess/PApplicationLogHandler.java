package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;

public class PApplicationLogHandler extends PLogHandler
{
	@Override
	protected String getLog( PersistentProcess pProcessInternal )
	{
		return pProcessInternal.getApplicationLog();
	}

	@Override
	protected void setLog( PersistentProcess pProcessInternal, String log )
	{
		pProcessInternal.setApplicationLog( log );
	}
}
