package ch.shaktipat.saraswati.example.polymorphicProcess;

import java.util.logging.Logger;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.pobject.PProcessStatic;

@PersistentClass
public class DefaultProcess extends AbstractProcess
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = PProcessStatic.getApplicationLogger( DefaultProcess.class.getName() );

	@Override
	protected void processStep1()
	{
		logger.info( "DefaultProcess.processStep1()" );
	}

	@Override
	protected void processStep2()
	{
		logger.info( "DefaultProcess.processStep2()" );
	}

	@Override
	protected void processStep3()
	{
		logger.info( "DefaultProcess.processStep3()" );
	}
}
