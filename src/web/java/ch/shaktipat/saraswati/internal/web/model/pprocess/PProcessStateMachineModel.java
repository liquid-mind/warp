package ch.shaktipat.saraswati.internal.web.model.pprocess;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import ch.shaktipat.saraswati.pobject.oid.PObjectOID;

public class PProcessStateMachineModel extends PProcessListRowModel
{
	private List< String > activeStates;

	public PProcessStateMachineModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate, Principal owner, String state, String volatility, List< String > activeStates )
	{
		super( commandSuccess, oid, name, createDate, owner, state, volatility );
		this.activeStates = activeStates;
	}

	public List< String > getActiveStates()
	{
		return activeStates;
	}

	public void setActiveStates( List< String > activeStates )
	{
		this.activeStates = activeStates;
	}

	@Override
	public String toString()
	{
		return "PProcessStateMachineModel [activeStates=" + activeStates + "]";
	}
}
