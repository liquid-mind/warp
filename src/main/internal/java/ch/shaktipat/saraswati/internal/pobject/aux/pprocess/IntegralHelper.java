package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.util.HashSet;
import java.util.Set;

public class IntegralHelper
{
	public static final Set< Class< ? > > INTEGRAL_TYPES = new HashSet< Class< ? > >();

	static
	{
		INTEGRAL_TYPES.add( int.class );
		INTEGRAL_TYPES.add( byte.class );
		INTEGRAL_TYPES.add( char.class );
		INTEGRAL_TYPES.add( short.class );
		INTEGRAL_TYPES.add( boolean.class );
	}

	private static final int JVM_TRUE = 1;
	private static final int JVM_FALSE = 0;
	
	public static void adjustParameterTypes( Class< ? >[] paramTypes, Object[] params )
	{
		for ( int i = 0 ; i < paramTypes.length ; ++i )
		{
			Class< ? > paramType = paramTypes[ i ];

			// Skip any non-integral types.
			if ( !INTEGRAL_TYPES.contains( paramType ) )
				continue;
			
			// Skip int, since it doesn't need conversion.
			if ( paramType.equals( int.class ) )
				continue;
			
			// Skip any types that don't need conversion
			// (all values coming back from the VM do need
			// conversion, but values being passed in from
			// outside don't; this only occurs during testing.).
			if ( !(params[ i ] instanceof Integer) )
				continue;

			// Otherwise, convert.
			int paramAsInt = ((Integer)params[ i ]).intValue();
			
			if ( paramType.equals( byte.class ) )
				params[ i ] = Byte.valueOf( (byte)paramAsInt );
			else if ( paramType.equals( char.class ) )
				params[ i ] = Character.valueOf( (char)paramAsInt );
			else if ( paramType.equals( short.class ) )
				params[ i ] = Short.valueOf( (short)paramAsInt );
			else if ( paramType.equals( boolean.class ) )
				params[ i ] = ( paramAsInt == JVM_TRUE ? Boolean.TRUE : Boolean.FALSE );
			else
				throw new IllegalStateException( "Unexpected value for paramType: " + paramType );
		}
	}

	public static Object adjustReturnType( Class< ? > returnType, Object retVal )
	{
		Object convertedRetVal = retVal;
		
		if ( INTEGRAL_TYPES.contains( returnType ) && !returnType.equals( int.class ) )
		{
			if ( returnType.equals( byte.class ) )
				convertedRetVal = Integer.valueOf( ((Byte)retVal).byteValue() );
			else if ( returnType.equals( char.class ) )
				convertedRetVal = Integer.valueOf( ((Character)retVal).charValue() );
			else if ( returnType.equals( short.class ) )
				convertedRetVal = Integer.valueOf( ((Short)retVal).shortValue() );
			else if ( returnType.equals( boolean.class ) )
				convertedRetVal = ( retVal.equals( Boolean.TRUE ) ? JVM_TRUE : JVM_FALSE );
			else
				throw new IllegalStateException( "Unexpected value for returnType: " + returnType );
		}
			
		return convertedRetVal;
	}
}
