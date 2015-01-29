package ch.shaktipat.saraswati.test.pobject.pqueue.sendAndListen;

import ch.shaktipat.exwrapper.java.lang.ThreadWrapper;

// Note that the only point of this class is to avoid directly
// referencing the java.lang.Thread API, since this will eventually
// be illegal (the Verifier will throw an exception); at least, that's
// what I anticipate.
public class ThreadProxy
{
	private Thread thread;
	
	public ThreadProxy( Runnable runnable )
	{
		thread = new Thread( runnable );
	}
	
	public void start()
	{
		thread.start();
	}
	
	public void join()
	{
		ThreadWrapper.join( thread );
	}
}
