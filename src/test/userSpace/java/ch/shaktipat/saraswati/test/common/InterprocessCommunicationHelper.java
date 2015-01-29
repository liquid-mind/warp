package ch.shaktipat.saraswati.test.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class InterprocessCommunicationHelper
{
	public static final Map< PProcessOID, Map< String, Object > > processOIDsToPropertiesMap = new HashMap< PProcessOID, Map< String, Object > >();

	public static void setProperty( String key, Object value )
	{
		setProperty( PersistentProcessImpl.getCurrentProcessOID(), key, value );
	}
	
	public static void setProperty( PProcessOID pProcessOID, String key, Object value )
	{
		Map< String, Object > properties = getProperties( pProcessOID );
		properties.put( key, value );
	}
	
	private static Map< String, Object > getProperties( PProcessOID pProcessOID )
	{
		Map< String, Object > properties = processOIDsToPropertiesMap.get( pProcessOID );
		
		if ( properties == null )
		{
			properties = new HashMap< String, Object >();
			processOIDsToPropertiesMap.put( pProcessOID, properties );
		}
		
		return properties;
	}
	
	public static Object getProperty( String key )
	{
		return getProperty( PersistentProcessImpl.getCurrentProcessOID(), key );
	}
	
	public static Object getProperty( PProcessOID pProcessOID, String key )
	{
		Map< String, Object > properties = getProperties( pProcessOID );
		Object value = properties.get( key );
		
		return value;
	}
	
	@SuppressWarnings( "unchecked" )
	public static void addToList( PProcessOID pProcessOID, String key, String value )
	{
		Map< String, Object > properties = getProperties( pProcessOID );
		Object currentValue = properties.get( key );
		
		if ( currentValue == null )
		{
			currentValue = new ArrayList< String >();
			properties.put( key, currentValue );
		}
		else if ( !(currentValue instanceof List) )
		{
			throw new RuntimeException( "Unexpected value type: expected=" + List.class.getName() +
				", acutal=" + currentValue.getClass().getName() );
		}
			
		List< String > stringList = (List< String >)currentValue;
		stringList.add( value );
	}

	public static List< String > getList( String key, String value )
	{
		return getList( PersistentProcessImpl.getCurrentProcessOID(), key );
	}
	
	@SuppressWarnings( "unchecked" )
	public static List< String > getList( PProcessOID pProcessOID, String key )
	{
		Map< String, Object > properties = getProperties( pProcessOID );
		List< String > value = (List< String >)properties.get( key );
		
		return value;
	}
}
