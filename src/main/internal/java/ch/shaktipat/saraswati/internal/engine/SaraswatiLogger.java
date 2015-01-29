package ch.shaktipat.saraswati.internal.engine;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PFrame;

public class SaraswatiLogger extends Logger
{
	protected SaraswatiLogger( String name, String resourceBundleName )
	{
		super( name, resourceBundleName );
	}

	@Override
	public void log( LogRecord record )
	{
		PFrame pFrame = PersistentProcessImpl.getCurrentPFrameOfCurrentProcess();
		StackTraceElement stackTraceElement = null;
		
		// When invoked in the context of a persistent process --> adjust the log record
		// to reflect the persistent class and method names.
		if ( pFrame != null )
			stackTraceElement = pFrame.getStackTraceElement();
		else
			stackTraceElement = Thread.currentThread().getStackTrace()[ 0 ];

		record.setSourceClassName( stackTraceElement.getClassName() );
		record.setSourceMethodName( stackTraceElement.getMethodName() );
		
		super.log( record );
	}
}
