package ch.shaktipat.exwrapper.java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import ch.shaktipat.exwrapper.java.lang.IllegalAccessExceptionWrapper;
import ch.shaktipat.exwrapper.java.lang.NoSuchMethodExceptionWrapper;

public class MethodHandlesWrapper
{
	public static class LookupWrapper
	{
		public static MethodHandle findSpecial( MethodHandles.Lookup lookup, Class<?> refc, String name, MethodType type, Class<?> specialCaller )
			{
				try
				{
					return lookup.findSpecial( refc, name, type, specialCaller );
				}
				catch ( NoSuchMethodException e )
				{
					throw new NoSuchMethodExceptionWrapper( e );
				}
				catch ( IllegalAccessException e )
				{
					throw new IllegalAccessExceptionWrapper( e );
				}
			}
	}
}
