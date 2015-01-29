package ch.shaktipat.saraswati.internal.engine;

import java.util.concurrent.ThreadFactory;

public class SaraswatiThreadFactory implements ThreadFactory
{
	@Override
	public Thread newThread( Runnable runnable )
	{
		return new SaraswatiThread( runnable );
	}
}
