package ch.shaktipat.saraswati.internal.web.transform;


public interface Transformer< T >
{
	public String getString( T obj );
	public T getValue( String objAsString );
}
