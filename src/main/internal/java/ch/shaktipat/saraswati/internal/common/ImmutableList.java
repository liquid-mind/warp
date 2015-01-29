package ch.shaktipat.saraswati.internal.common;

import java.util.ArrayList;
import java.util.Collection;

public class ImmutableList< T > extends ArrayList< T >
{
	private static final long serialVersionUID = 1L;

	@Override public void trimToSize() { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public void ensureCapacity( int minCapacity ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public T set( int index, T element ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean add( T e ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public void add( int index, T element ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public T remove( int index ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean remove( Object o ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public void clear() { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean addAll( Collection< ? extends T > c ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean addAll( int index, Collection< ? extends T > c ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override protected void removeRange( int fromIndex, int toIndex ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean removeAll( Collection< ? > c ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
	@Override public boolean retainAll( Collection< ? > c ) { throw new UnsupportedOperationException( "Illegal operation: list is immutable" ); }
}
