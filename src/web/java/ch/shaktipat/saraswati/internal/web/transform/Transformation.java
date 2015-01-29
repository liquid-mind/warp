package ch.shaktipat.saraswati.internal.web.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

public class Transformation
{
	private static Map< Class< ? >, Transformer< ? > > transformerTypeToInstanceMap;
	
	static
	{
		transformerTypeToInstanceMap = new HashMap< Class< ? >, Transformer< ? > >();

		Reflections reflections = new Reflections( "" );
		Set< Class< ? > > transformerClasses = reflections.getTypesAnnotatedWith( TransformerMarker.class );

		for ( Class< ? > transformerClass : transformerClasses )
		{
			Transformer< ? > transformer = (Transformer< ? >)ClassWrapper.newInstance( transformerClass );
			transformerTypeToInstanceMap.put( transformerClass, transformer );
		}
	}

	public static < T > String getString(  Class< ? > transformerType, T value )
	{
		return getTransformer( transformerType ).getString( value );
	}

	@SuppressWarnings( "unchecked" )
	public static < T > T getValue( Class< ? > transformerType, String objectAsString )
	{
		return (T)getTransformer( transformerType ).getValue( objectAsString );
	}
	
	@SuppressWarnings( "unchecked" )
	private static < T > Transformer< T > getTransformer( Class< ? > transformerType )
	{
		return (Transformer< T >)transformerTypeToInstanceMap.get( transformerType );
	}
}
