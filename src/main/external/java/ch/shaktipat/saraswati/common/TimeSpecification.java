package ch.shaktipat.saraswati.common;

import java.io.Serializable;
import java.util.Date;

public abstract class TimeSpecification implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public abstract Date calcDeadline();
}
