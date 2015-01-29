package ch.shaktipat.saraswati.internal.common;

import java.util.Collection;

public class CollectionHelper
{
	public static < T > T removeAny( Collection< T > collection )
	{
		T item = null;
		
		if ( collection.iterator().hasNext() )
			item = collection.iterator().next();
		
		return item;
	}
}
