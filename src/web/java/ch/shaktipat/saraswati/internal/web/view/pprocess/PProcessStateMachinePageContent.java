package ch.shaktipat.saraswati.internal.web.view.pprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PersistentProcessStateMachine;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.model.pprocess.PProcessStateMachineModel;
import ch.shaktipat.saraswati.internal.web.view.DetailPageContent;


public class PProcessStateMachinePageContent extends DetailPageContent
{
	private static final int TOP_OFFSET = 8;
	private static final int LEFT_OFFSET = 13;
	
	private static final Set< ProcessState > ALL_STATES = new HashSet< ProcessState >();
	
	static
	{
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.REFERENCABLE_STATE, 5, 170 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.PENDING_STATE, 32, 172 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.SUSPENDED_STATE, 52, 54 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.ANIMATED_STATE, 110, 170 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.WAITING_STATE, 158, 175 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.SLEEPING_STATE, 180, 57 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.JOINING_STATE, 180, 167 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.LISTENING_STATE, 180, 265 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.RUNNING_STATE, 270, 175 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.NEW_STATE, 417, 40 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.TERMINATED_STATE, 417, 235 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.CANCELLED_STATE, 438, 115 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.COMPLETED_STATE, 438, 190 ) );
		ALL_STATES.add( new ProcessState( PersistentProcessStateMachine.THREW_EXCEPTION_STATE, 438, 268 ) );
	}

	public PProcessStateMachinePageContent( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderSuccess()
	{
		Writer.write( "<div style='text-align: center;'>" );
		Writer.write( "<div style='position: relative; display: inline-block;'>" );
		
		// TODO replace style with proper class.
		// TODO put link address in WebConstants.
		Writer.write( "<figure>" );
		Writer.write( "<img src='/ch/shaktipat/saraswati/internal/web/ui/persistent process state machine.png'/>" );
		Writer.write( "<figcaption>State machine for process with OID<br>" + getModel().getOid() );
		Writer.write( "</figure>" );

		List< String > activeStates = getModel().getActiveStates();
		
		for ( ProcessState state : ALL_STATES )
		{
			String activationStyle = "color: #888888";
			
			if ( activeStates.contains( state.getName() ) )
				activationStyle = "color: #6666FF; font-weight: bold;";
			
			int top = TOP_OFFSET + state.getTop();
			int left = LEFT_OFFSET + state.getLeft();
				
			Writer.write( "<span style='position: absolute; top: " + top + "px; left: " + left + "px; font-size: 9pt; " + activationStyle + "'>" + state.getName() + "</span>" );
		}

		Writer.write( "</div>" );
		Writer.write( "</div>" );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected PProcessStateMachineModel getModel()
	{
		return super.getModel();
	}
	
	private static class ProcessState
	{
		private String name;
		private int top;
		private int left;
		
		public ProcessState( String name, int top, int left )
		{
			super();
			this.name = name;
			this.top = top;
			this.left = left;
		}

		public String getName()
		{
			return name;
		}

		public int getTop()
		{
			return top;
		}

		public int getLeft()
		{
			return left;
		}
	}
}
