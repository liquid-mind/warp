package ch.shaktipat.saraswati.volatility;

import java.io.Serializable;

public enum Volatility implements Serializable
{
	DYNAMIC, VOLATILE, STABLE;
	
	public static Volatility getDefault()
	{
		return DYNAMIC;
	}
}
