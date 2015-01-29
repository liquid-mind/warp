package ch.shaktipat.saraswati.internal.engine;

public class ShutdownThread extends Thread
{
	@Override
	public void run()
	{
		PEngine.shutdown();
		SaraswatiLogManager.sarswatiReset();
	}
}
