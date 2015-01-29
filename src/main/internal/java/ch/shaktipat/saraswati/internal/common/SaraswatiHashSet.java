package ch.shaktipat.saraswati.internal.common;

import java.util.HashSet;

public class SaraswatiHashSet< T > extends HashSet< T >
{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString()
	{
		String mapAsString = this.getClass().getName() + "@" + this.hashCode() + System.lineSeparator();
		
		mapAsString += "{" + System.lineSeparator();
		
		for ( T value : this )
			mapAsString += "\t" + value.toString() + System.lineSeparator();
		
		mapAsString += "}" + System.lineSeparator();
		
		return mapAsString;
	}
	
	public void printForDebuggging()
	{
		System.out.println( toString() );
	}
	
}
