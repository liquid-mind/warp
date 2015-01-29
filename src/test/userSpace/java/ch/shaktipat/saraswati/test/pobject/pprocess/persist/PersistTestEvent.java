package ch.shaktipat.saraswati.test.pobject.pprocess.persist;

import ch.shaktipat.saraswati.common.AbstractEvent;

public class PersistTestEvent extends AbstractEvent
{
	private static final long serialVersionUID = 1L;

	private String msg;

	public PersistTestEvent( String msg )
	{
		super();
		this.msg = msg;
	}

	public String getMsg()
	{
		return msg;
	}
}
