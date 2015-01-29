package ch.shaktipat.saraswati.test.rmi;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.rmi.SaraswatiRMIServer;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.manager.PProcessManager;
import ch.shaktipat.saraswati.volatility.Volatility;

public class PProcessServiceUserSpaceTest
{
	public void test() throws RemoteException, NotBoundException, MalformedURLException
	{
		SaraswatiRMIServer.start();
		
		PProcessManager pProcessManager = PEnvironmentFactory.createRemote( "localhost", 1099 ).getPProcessManager();
		
		PProcessHandle pProcessHandle = pProcessManager.create( new TestRunnable(), "test", Volatility.DYNAMIC, null, false, false );
		String[] states = pProcessHandle.getStates();
		System.out.println( "States: " + StringUtils.join( states, ", " ) );
		pProcessHandle.start();
		states = pProcessHandle.getStates();
		System.out.println( "States: " + StringUtils.join( states, ", " ) );
		pProcessHandle.join();
		pProcessHandle.destroy();
		
		SaraswatiRMIServer.stop();
	}
}
