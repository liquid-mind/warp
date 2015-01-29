package ch.shaktipat.saraswati.internal.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import ch.shaktipat.exwrapper.java.io.ObjectInputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.ObjectOutputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.OutputStreamWrapper;

public class SerializationHelper
{
	public static Object[] cloneObjects( Object[] sourceObjects )
	{
		Object[] cloneObjects = new Object[ sourceObjects.length ];
		
		for ( int i = 0 ; i < sourceObjects.length ; ++i )
			cloneObjects[ i ] = cloneObject( sourceObjects[ i ] );
		
		return cloneObjects;
	}
	
	// TODO shouldn't we be closing these streams (also check
	// serializeObject()/deserializeObject() below)?
	public static Object cloneObject( Object sourceObject )
	{
		// Write object to byte[].
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializeObject( sourceObject, baos );
		byte[] bytes = baos.toByteArray();
		
		// Read object from byte[]
		ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
		Object clonedObject = deserializeObject( bais );
		
		return clonedObject;
	}

	public static void serializeObject( Object obj, OutputStream outputStream )
	{
		ObjectOutputStream ooStream = ObjectOutputStreamWrapper.__new( outputStream );
		ObjectOutputStreamWrapper.writeObject( ooStream, obj );
		ObjectOutputStreamWrapper.flush( ooStream );
		OutputStreamWrapper.flush( outputStream );
	}
	
	public static Object deserializeObject( InputStream inputStream )
	{
		ClassLoaderAwareObjectInputStream soiStream = ClassLoaderAwareObjectInputStream.create( inputStream );
		Object deserializedObj = ObjectInputStreamWrapper.readObject( soiStream );
		
		return deserializedObj;
	}
}
