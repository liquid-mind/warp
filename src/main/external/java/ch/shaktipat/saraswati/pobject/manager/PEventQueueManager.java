package ch.shaktipat.saraswati.pobject.manager;

import java.util.List;

import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.pobject.PEventQueue;
import ch.shaktipat.saraswati.pobject.oid.PEventQueueOID;

public interface PEventQueueManager extends POwnableObjectManager
{
	public PEventQueue create();
	public PEventQueue create( Scope scope );	
	public PEventQueue create( String name, Scope scope );

	public PEventQueue findByOID( PEventQueueOID oid );
	public List< PEventQueue > findByName( String name );	
}
