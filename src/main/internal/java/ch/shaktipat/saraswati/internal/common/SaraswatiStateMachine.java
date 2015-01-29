package ch.shaktipat.saraswati.internal.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.TransitionTarget;

public abstract class SaraswatiStateMachine extends AbstractStateMachine
{
	private static final long serialVersionUID = 1L;

	public SaraswatiStateMachine( SCXML stateMachine )
	{
		super( stateMachine );
	}

	protected static class BeforeStates extends HashSet< String >
	{
		private static final long serialVersionUID = 1L;
		
		public BeforeStates( String ... states )
		{
			for ( String state : states )
				add( state );
		}
	}
	
	protected static class AfterStates extends HashSet< String >
	{
		private static final long serialVersionUID = 1L;
		
		public AfterStates( String ... states )
		{
			for ( String state : states )
				add( state );
		}
	}
	
	protected static class BeforeAndAfterStates
	{
		public BeforeStates beforeStates;
		public AfterStates afterStates;

		public BeforeAndAfterStates( BeforeStates beforeStates, AfterStates afterStates )
		{
			this.beforeStates = beforeStates;
			this.afterStates = afterStates;
		}
	}
	
	public Set< String > getStates()
	{
		Set< String > statesAsString = new HashSet< String >();
		
		@SuppressWarnings( "unchecked" )
		Set< TransitionTarget > states = getEngine().getCurrentStatus().getAllStates();
		
		for ( TransitionTarget state : states )
			statesAsString.add( state.getId() );
		
		return statesAsString;
	}
	
	public Set< String > getLeafStates()
	{
		Set< String > statesAsString = new HashSet< String >();
		
		@SuppressWarnings( "unchecked" )
		Set< TransitionTarget > states = getEngine().getCurrentStatus().getStates();
		
		for ( TransitionTarget state : states )
			statesAsString.add( state.getId() );
		
		return statesAsString;
	}
	
	public boolean isInState( String state )
	{
		boolean isInState = false;
		
		if ( getStates().contains( state ) )
			isInState = true;
		
		return isInState;
	}
	
	public void validateState( String state )
	{
		if ( !isInState( state ) )
			throw new RuntimeException( "State machine expected to be in state: " + state + ", but was in states: " + getStates() );
	}
	
	@Override
	public boolean fireEvent( String event )
	{
		Set< String > actualStatesBefore = getStates();
		boolean isFinal = super.fireEvent( event );
		Set< String > actualStatesAfter = getStates();
		
		BeforeAndAfterStates baaStates = getEventToStatesMap().get( event );
		BeforeStates expectedStatesBefore = baaStates.beforeStates;
		AfterStates expectedStatesAfter = baaStates.afterStates;
		
		if ( !actualStatesBefore.containsAll( expectedStatesBefore ) )
			throw new RuntimeException( "Before event '" + event +"' state should have been '" + expectedStatesBefore + "' but was '" + actualStatesBefore + "'." );
		if ( !actualStatesAfter.containsAll( expectedStatesAfter ) )
			throw new RuntimeException( "After event '" + event +"' state should have been '" + expectedStatesAfter + "' but was '" + actualStatesAfter + "'." );
		
		return isFinal;
	}
	
	protected abstract Map< String, BeforeAndAfterStates > getEventToStatesMap();
	
	@Override
	protected void logError( Exception exception )
	{
		// We don't require all states to have corresponding methods --> ignore these exceptions
		if ( exception instanceof NoSuchMethodException )
			return;
		
		super.logError( exception );
	}

	@Override
	public String toString()
	{
		return StringUtils.join( getStates(), "," );
	}
}
