package ch.shaktipat.saraswati.test.pobject.psharedobject;

import java.io.Serializable;

public interface SharedObject extends Serializable
{
	public String testMethod( String msg );
}
