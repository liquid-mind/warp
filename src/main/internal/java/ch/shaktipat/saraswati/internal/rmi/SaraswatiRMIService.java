package ch.shaktipat.saraswati.internal.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public interface SaraswatiRMIService extends Remote
{
	public Object invokeOnPObjectManager( String className, String methodName, String[] paramTypeNames, Object[] args ) throws RemoteException;
	public Object invokeOnPObject( PObjectOID pObjectOID, String className, String methodName, String[] paramTypeNames, Object[] args ) throws RemoteException;
}
