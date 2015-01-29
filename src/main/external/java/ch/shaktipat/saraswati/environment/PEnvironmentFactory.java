package ch.shaktipat.saraswati.environment;

import ch.shaktipat.saraswati.internal.environment.PEnvironmentLocal;
import ch.shaktipat.saraswati.internal.environment.PEnvironmentRemote;

public class PEnvironmentFactory
{
	public static PEnvironment createLocal()
	{
		return new PEnvironmentLocal();
	}
	
	public static PEnvironment createRemote( String hostName, int port )
	{
		return new PEnvironmentRemote( hostName, port );
	}
}
