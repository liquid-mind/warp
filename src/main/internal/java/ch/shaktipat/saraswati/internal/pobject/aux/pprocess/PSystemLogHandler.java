package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;

public class PSystemLogHandler extends PLogHandler
{
	@Override
	protected String getLog( PersistentProcess pProcessInternal )
	{
		return pProcessInternal.getSystemLog();
	}

	@Override
	protected void setLog( PersistentProcess pProcessInternal, String log )
	{
		pProcessInternal.setSystemLog( log );
	}
}
