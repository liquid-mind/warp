package ch.shaktipat.saraswati.internal.environment;

import java.lang.reflect.Proxy;

import ch.shaktipat.saraswati.internal.dynproxies.PObjectManagerInvocationHandler;

public class PEnvironmentLocal extends PEnvironmentAbstract
{
	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings( "unchecked" )
	protected < T > T getPObjectManager( Class< ? > pObjectManagerInterface )
	{
		return (T)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class< ? >[] { pObjectManagerInterface }, new PObjectManagerInvocationHandler() );
	}
}
