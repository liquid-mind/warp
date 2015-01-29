package ch.shaktipat.saraswati.internal.engine;

import ch.shaktipat.saraswati.internal.rmi.SaraswatiRMIServer;
import ch.shaktipat.saraswati.internal.web.SaraswatiWebServer;

public class SaraswatiServer
{
	public static void main( String[] args )
	{
		SaraswatiRMIServer.start();
		SaraswatiWebServer.start();
	}
	
	public static void start()
	{
		PEngine.startup();
		SaraswatiRMIServer.start();
		SaraswatiWebServer.start();
	}
	
	public static void stop()
	{
		stopWithoutExit();
		System.exit( 0 );
	}
	
	private static void stopWithoutExit()
	{
		SaraswatiWebServer.stop();
		SaraswatiRMIServer.stop();
		PEngine.shutdown();
	}

	public static void restart()
	{
		stopWithoutExit();
		start();
	}
}
