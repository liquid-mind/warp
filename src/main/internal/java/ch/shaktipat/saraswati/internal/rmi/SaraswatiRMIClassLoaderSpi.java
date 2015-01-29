package ch.shaktipat.saraswati.internal.rmi;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;

import ch.shaktipat.saraswati.internal.classloading.JavaClassHelper;

public class SaraswatiRMIClassLoaderSpi extends RMIClassLoaderSpi
{
	@Override
	public Class< ? > loadClass( String codebase, String name, ClassLoader defaultLoader ) throws MalformedURLException, ClassNotFoundException
	{
		return JavaClassHelper.getClass( name );
	}

	@Override
	public Class< ? > loadProxyClass( String codebase, String[] interfaces, ClassLoader defaultLoader ) throws MalformedURLException, ClassNotFoundException
	{
		return RMIClassLoader.getDefaultProviderInstance().loadProxyClass( codebase, interfaces, defaultLoader );
	}

	@Override
	public ClassLoader getClassLoader( String codebase ) throws MalformedURLException
	{
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	public String getClassAnnotation( Class< ? > cl )
	{
		return RMIClassLoader.getDefaultProviderInstance().getClassAnnotation( cl );
	}
}
