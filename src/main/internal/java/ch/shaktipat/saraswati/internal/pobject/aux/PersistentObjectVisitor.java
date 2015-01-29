package ch.shaktipat.saraswati.internal.pobject.aux;

import ch.shaktipat.saraswati.internal.pobject.PersistentObject;


public interface PersistentObjectVisitor
{
	public boolean visit( PersistentObject persistentObject );
}
