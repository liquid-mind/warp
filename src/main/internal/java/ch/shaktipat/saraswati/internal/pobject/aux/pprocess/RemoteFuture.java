package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.lang.reflect.Proxy;

import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.rmi.PObjectRMIClientInvocationHandler;
import ch.shaktipat.saraswati.pobject.PProcessHandle;

public class RemoteFuture< T > extends PersistentProcessFuture< T >
{
	private static final long serialVersionUID = 1L;

	private String hostName;
	private int port;
	
	public RemoteFuture( PProcessHandle pProcessHandle, String hostName, int port )
	{
		super( pProcessHandle );
		this.hostName = hostName;
		this.port = port;
	}

	@Override
	protected PersistentProcess getPersistentProcess()
	{
		return (PersistentProcess)Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class< ? >[] { PersistentProcess.class }, new PObjectRMIClientInvocationHandler( getPProcessHandle().getOID(), hostName, port ) );
	}
}
