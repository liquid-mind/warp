package ch.shaktipat.saraswati.volatility;

import java.io.Serializable;

public interface VolatileResource extends Serializable
{
	public void enlist();
	public void deEnlist();
	public boolean isEnlisted();
}
