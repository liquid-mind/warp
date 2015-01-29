package ch.shaktipat.saraswati.test.rmi;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class TestRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {}
}
