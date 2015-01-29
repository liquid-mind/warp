package ch.shaktipat.saraswati.example.polymorphicProcess;

import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class DynamicProcess extends DefaultProcess
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( DynamicProcess.class.getName() );
	
	private DynamicType dynamicType;

	public DynamicProcess( DynamicType dynamicType )
	{
		super();
		this.dynamicType = dynamicType;
	}

	@Override
	protected void processStep2()
	{
		if ( dynamicType.equals( DynamicType.FULLY_AUTOMATED ) )
			processStep2FullyAutomated();
		else if ( dynamicType.equals( DynamicType.SEMI_AUTOMATED ) )
			processStep2SemiAutomated();
		else
			throw new IllegalStateException();
	}
	
	private void processStep2FullyAutomated()
	{
		logger.info( "DynamicProcess.processStep2FullyAutomated()" );
	}
	
	private void processStep2SemiAutomated()
	{
		startExternalProcess();
		joinExternalProcess();
	}
	
	private void startExternalProcess()
	{
		logger.info( "DynamicProcess.startExternalProcess()" );
	}
	
	private void joinExternalProcess()
	{
		logger.info( "DynamicProcess.joinExternalProcess(): waiting for event..." );
		PProcessStatic.currentProcess().listen();
		logger.info( "DynamicProcess.joinExternalProcess(): ...event arrived" );
	}
}
