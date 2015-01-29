package ch.shaktipat.saraswati.internal.engine;

public class SaraswatiThread extends Thread
{
	public SaraswatiThread( Runnable runnable )
	{
		super( runnable );
		setName( SaraswatiThread.class.getSimpleName() + "-" + getId() );
	}
}
