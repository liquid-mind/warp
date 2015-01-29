package ch.shaktipat.saraswati.pobject;

import java.io.Serializable;
import java.util.Date;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public interface PObject extends Serializable
{
	public PObjectOID getOID();
	public boolean exists();
	public void destroy();
	public Date getCreateDate();
	public Date getLastActivityDate();
	public boolean isPersistent();
	public String getName();
}
