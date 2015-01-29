package ch.shaktipat.saraswati.internal.common;

import java.util.HashMap;
import java.util.Map;

public class SaraswatiHashMap< K, V > extends HashMap< K, V >
{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString()
	{
		String mapAsString = this.getClass().getName() + "@" + this.hashCode() + System.lineSeparator();
		
		mapAsString += "{" + System.lineSeparator();
		
		for ( Map.Entry< K, V > entry : this.entrySet() )
			mapAsString += "\t" + entry.getKey().toString() + " = " + entry.getValue().toString() + System.lineSeparator();
		
		mapAsString += "}" + System.lineSeparator();
		
		return mapAsString;
	}
	
	public void printForDebuggging()
	{
		System.out.println( toString() );
	}
}
