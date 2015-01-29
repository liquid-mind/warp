package ch.shaktipat.saraswati.internal.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;

public class WebConstants
{
	public static final DateFormat STANDARD_DATE_FORMAT;
	public static final DateFormat YEAR_ONLY_DATE_FORMAT;

	public static final Set< String > VOLATILITIES;
	
	static
	{
		STANDARD_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		YEAR_ONLY_DATE_FORMAT = new SimpleDateFormat("yyyy");

		VOLATILITIES = new HashSet< String >();
		VOLATILITIES.add( PersistentProcessStateMachine.VOLATILE_STATE );
		VOLATILITIES.add( PersistentProcessStateMachine.STABLE_STATE );
	}
}
