package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import ch.shaktipat.exwrapper.java.io.CloseableWrapper;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.pobject.PProcess;

public abstract class PLogHandler extends Handler
{
	private StringWriter stringWriter;
	private Formatter formatter;

	public PLogHandler()
	{
		super();
		stringWriter = new StringWriter();
		formatter = new SimpleFormatter();
	}

	@Override
	public void publish( LogRecord record )
	{
		PProcess pProcess = PersistentProcessImpl.getCurrentProcess();
		
		if ( pProcess != null )
		{
			PersistentProcess pProcessInternal = PersistentProcessImpl.getOtherProxy( pProcess.getOID() );
			String sourceClassName = PEngine.class.getName();
			String sourceMethodName = "";
			
			StackTraceElement[] stackTrace = pProcessInternal.getStackTrace();
			
			if ( stackTrace.length > 0 )
			{
				sourceClassName = stackTrace[ 0 ].getClassName();
				sourceMethodName = stackTrace[ 0 ].getMethodName();
			}

			record.setSourceClassName( sourceClassName );
			record.setSourceMethodName( sourceMethodName );
			
			stringWriter.write( formatter.format( record ) );
			stringWriter.flush();

			String log = getLog( pProcessInternal );
			log += stringWriter.toString();
			setLog( pProcessInternal, log );

			stringWriter.getBuffer().setLength( 0 );
		}
	}

	@Override
	public void close() throws SecurityException
	{
		CloseableWrapper.close( stringWriter );
	}
	
	protected abstract String getLog( PersistentProcess pProcessInternal );
	protected abstract void setLog( PersistentProcess pProcessInternal, String log );

	@Override
	public void flush()
	{}
}
