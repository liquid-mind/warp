package ch.shaktipat.saraswati.test.common;

import java.lang.reflect.Method;

import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.exwrapper.java.lang.reflect.MethodWrapper;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class InterprocessCommunicationHelperProxy
{
	public static String getProperty( PProcessOID pProcessOID, String key )
	{
		Class< ? > ipCommHelperClass = ClassWrapper.forName( InterprocessCommunicationHelper.class.getName(),
			true, PEngine.getPEngine().getPClassLoader() );
		Method method = ClassWrapper.getMethod( ipCommHelperClass, "getProperty", PProcessOID.class, String.class );
		String value = (String)MethodWrapper.invoke( method, null, pProcessOID, key );
		
		return value;
	}
}
