package ch.shaktipat.saraswati.internal.environment;

import java.lang.reflect.Proxy;

import ch.shaktipat.saraswati.internal.rmi.PObjectManagerRMIClientInvocationHandler;

public class PEnvironmentRemote extends PEnvironmentAbstract
{
	private static final long serialVersionUID = 1L;
	
	private String hostName;
	private int port;

	public PEnvironmentRemote( String hostName, int port )
	{
		super();
		this.hostName = hostName;
		this.port = port;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected < T > T getPObjectManager( Class< ? > pObjectManagerInterface )
	{
		return (T)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class< ? >[] { pObjectManagerInterface }, new PObjectManagerRMIClientInvocationHandler( hostName, port ) );
	}
}
