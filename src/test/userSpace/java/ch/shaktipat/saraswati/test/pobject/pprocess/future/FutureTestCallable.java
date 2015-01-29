package ch.shaktipat.saraswati.test.pobject.pprocess.future;

import java.io.Serializable;
import java.util.concurrent.Callable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class FutureTestCallable implements Callable< String >, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String msg;
	
	public FutureTestCallable( String msg )
	{
		this.msg = msg;
	}

	@Override
	public String call() throws Exception
	{
		if ( msg == null )
			throw new FutureTestException();
		
		return msg + " polo!";
	}
}
