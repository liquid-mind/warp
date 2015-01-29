package ch.shaktipat.saraswati.example.lifecycleAndFutures;

import java.io.Serializable;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class LifecycleAndFuturesRunnable implements Callable< String >, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public String call()
	{
		return "hello world!";
	}
}
