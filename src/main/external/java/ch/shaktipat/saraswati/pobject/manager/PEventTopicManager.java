package ch.shaktipat.saraswati.pobject.manager;

import java.util.List;

import ch.shaktipat.saraswati.common.Scope;
import ch.shaktipat.saraswati.pobject.PEventTopic;
import ch.shaktipat.saraswati.pobject.oid.PEventTopicOID;

public interface PEventTopicManager extends POwnableObjectManager
{
	public PEventTopic create();
	public PEventTopic create( Scope scope);
	public PEventTopic create( String name, Scope scope );

	public PEventTopic findByOID( PEventTopicOID oid );
	public List< PEventTopic > findByName( String name );	
}
