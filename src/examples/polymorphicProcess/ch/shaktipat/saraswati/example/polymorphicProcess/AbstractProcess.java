package ch.shaktipat.saraswati.example.polymorphicProcess;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public abstract class AbstractProcess implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void run()
	{
		processStep1();
		processStep2();
		processStep3();
	}
	
	protected abstract void processStep1();
	protected abstract void processStep2();
	protected abstract void processStep3();
}
