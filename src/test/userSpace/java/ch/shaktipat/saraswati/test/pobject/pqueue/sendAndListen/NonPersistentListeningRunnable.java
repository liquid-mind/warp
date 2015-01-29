package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import ch.shaktipat.saraswati.common.Event;
import ch.shaktipat.saraswati.pobject.PEventQueue;

public class NonPersistentListeningRunnable implements Runnable
{
	private PEventQueue queue;
	private Event receivedEvent;
	
	public NonPersistentListeningRunnable( PEventQueue queue )
	{
		super();
		this.queue = queue;
	}

	@Override
	public void run()
	{
		receivedEvent = queue.listen();
	}

	public Event getReceivedEvent()
	{
		return receivedEvent;
	}
}
