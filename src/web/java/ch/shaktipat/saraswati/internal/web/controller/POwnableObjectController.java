package ch.shaktipat.saraswati.internal.web.controller;

import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public abstract class POwnableObjectController extends Controller
{
	protected PProcessOID getProcessOID( PProcessHandle handle )
	{
		PProcessOID pProcessOID = null;
		
		if ( handle != null )
			pProcessOID = handle.getOID();
		
		return pProcessOID;
	}
	
	protected String getProcessName( PProcessHandle handle )
	{
		String pProcessName = null;
		
		if ( handle != null )
			pProcessName = handle.getName();
		
		return pProcessName;
	}
}
