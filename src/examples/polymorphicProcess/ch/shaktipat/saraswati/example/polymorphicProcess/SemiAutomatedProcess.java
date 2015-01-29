package ch.shaktipat.saraswati.example.polymorphicProcess;

import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class SemiAutomatedProcess extends DefaultProcess
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( FullyAutomatedProcess.class.getName() );
	
	@Override
	protected void processStep2()
	{
		startExternalProcess();
		joinExternalProcess();
	}
	
	private void startExternalProcess()
	{
		logger.info( "SemiAutomatedProcess.startExternalProcess()" );
	}
	
	private void joinExternalProcess()
	{
		logger.info( "SemiAutomatedProcess.joinExternalProcess(): waiting for event..." );
		PProcessStatic.currentProcess().listen();
		logger.info( "SemiAutomatedProcess.joinExternalProcess(): ...event arrived" );
	}
}
