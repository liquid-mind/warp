package ch.shaktipat.saraswati.internal.classloading;

import java.net.URL;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import ch.shaktipat.exwrapper.javassist.ClassPoolWrapper;
import ch.shaktipat.saraswati.internal.engine.PEngine;

public class PClassPool extends ClassPool
{
	private LoaderClassPath systemClassPath = null;
	private LoaderClassPath extensionClassPath = null;
	
	public PClassPool()
	{
		systemClassPath = new LoaderClassPath( PClassLoader.getSystemClassLoader() );
		extensionClassPath = new LoaderClassPath( PClassLoader.getSystemClassLoader().getParent() );
		
		insertClassPath( systemClassPath );
		insertClassPath( extensionClassPath );
		
		for ( URL url : PEngine.getPEngine().getSaraswatiConfiguration().getUserClasspath() )
		{
			String classPath = url.getFile();
			ClassPoolWrapper.insertClassPath( this, classPath );
		}
	}
	
	public boolean isClassInSaraswatiClassPath( String className )
	{
		boolean isClassInSaraswatiClassPath = true;
		
		if ( systemClassPath.find( className ) != null )
			isClassInSaraswatiClassPath = false;
		else if ( extensionClassPath.find( className ) != null )
			isClassInSaraswatiClassPath = false;
			
		return isClassInSaraswatiClassPath;
	}
	
}
