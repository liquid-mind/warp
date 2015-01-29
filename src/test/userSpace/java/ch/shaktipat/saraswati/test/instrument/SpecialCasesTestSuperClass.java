package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class SpecialCasesTestSuperClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public int incByOne( int i )
	{
		return i + 1;
	}
}
