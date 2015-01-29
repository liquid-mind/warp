package ch.shaktipat.saraswati.example.polymorphicProcess;

import java.io.IOException;

import ch.shaktipat.saraswati.common.BasicEvent;
import ch.shaktipat.saraswati.environment.PEnvironment;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;

public class PolymorphicClient
{
	public static void main( String[] args ) throws IOException
	{
		PEnvironment env = PEnvironmentFactory.createRemote( "localhost", 1099 );
		PProcessManager pProcessManager = env.getPProcessManager();
		
		if ( args[ 0 ].equals( "startProcesses" ) )
		{
			PProcessHandle faHandle = pProcessManager.create( new FullyAutomatedProcess(), "Fully-Automated Process" );
			faHandle.start();
			faHandle.join();

			PProcessHandle saHandle = pProcessManager.create( new SemiAutomatedProcess(), "Semi-Automated Process" );
			saHandle.start();

			PProcessHandle dfaHandle = pProcessManager.create( new DynamicProcess( DynamicType.FULLY_AUTOMATED ),
				"Dynamic Process: " + DynamicType.FULLY_AUTOMATED );
			dfaHandle.start();
			dfaHandle.join();

			PProcessHandle dsaHandle = pProcessManager.create( new DynamicProcess( DynamicType.SEMI_AUTOMATED ),
				"Dynamic Process: " + DynamicType.SEMI_AUTOMATED );
			dsaHandle.start();
		}
		else
		{
			BasicEvent extenalProcessFinishedEvent = new BasicEvent();

			PProcessHandle saHandle = pProcessManager.findByName( "Semi-Automated Process" ).get( 0 );
			saHandle.send( extenalProcessFinishedEvent );
			saHandle.join();

			PProcessHandle dsaHandle = pProcessManager.findByName( "Dynamic Process: " + DynamicType.SEMI_AUTOMATED ).get( 0 );
			dsaHandle.send( extenalProcessFinishedEvent );
			saHandle.join();
		}
	}
}
