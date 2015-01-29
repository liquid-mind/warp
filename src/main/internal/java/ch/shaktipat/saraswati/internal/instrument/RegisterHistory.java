package ch.shaktipat.saraswati.internal.instrument;

import java.util.HashSet;
import java.util.Set;

import javassist.CtMethod;
import javassist.bytecode.LocalVariableAttribute;

// The purpose of this class is that individual registers *can* be associated
// with multiple local variables during the execution of a method (although they
// usually don't seem to be). This class provides a full picture of which bytecode
// ranges are associated with local variables.
public class RegisterHistory
{
	private int register;
	private Set< RegisterHistoryEntry > registerHistoryEntries;
	
	public RegisterHistory( int register )
	{
		super();
		this.register = register;
		registerHistoryEntries = new HashSet< RegisterHistoryEntry >();
	}

	public static Set< RegisterHistory > getRegisterHistories( CtMethod ctMethod )
	{
		Set< RegisterHistory > registerHistories = new HashSet< RegisterHistory >();
		
		LocalVariableAttribute lvAttribute = (LocalVariableAttribute)ctMethod.getMethodInfo().getCodeAttribute().getAttribute( LocalVariableAttribute.tag );
		
		if ( lvAttribute == null )
			return registerHistories;
		
		for ( int i = 0 ; i < lvAttribute.tableLength() ; ++i )
		{
			int from = lvAttribute.startPc( i );
			int to = from + lvAttribute.codeLength( i );
			int register = lvAttribute.index( i );
			
			RegisterHistory registerHistory = getRegisterHistory( registerHistories, register );
			registerHistory.addRegisterHistoryEntry( from, to );
		}
		
		return registerHistories;
	}
	
	private static RegisterHistory getRegisterHistory( Set< RegisterHistory > registerHistories, int register )
	{
		RegisterHistory registerHistory = null;
		
		for ( RegisterHistory currentHistory : registerHistories )
		{
			if ( currentHistory.getRegister() == register )
			{
				registerHistory = currentHistory;
				break;
			}
		}
		
		if ( registerHistory == null )
		{
			registerHistory = new RegisterHistory( register );
			registerHistories.add( registerHistory );
		}
		
		return registerHistory;
	}

	public int getRegister()
	{
		return register;
	}
	
	public void addRegisterHistoryEntry( int from, int to )
	{
		registerHistoryEntries.add( new RegisterHistoryEntry( from, to ) );
	}
	
	public boolean isInRange( int index )
	{
		boolean isInRange = false;
		
		for ( RegisterHistoryEntry entry : registerHistoryEntries )
		{
			if ( index >= entry.getFrom() && index < entry.getTo() )
			{
				isInRange = true;
				break;
			}
		}
		
		return isInRange;
	}
	
	public static class RegisterHistoryEntry
	{
		private int from;
		private int to;
		
		public RegisterHistoryEntry( int from, int to )
		{
			super();
			this.from = from;
			this.to = to;
		}

		public int getFrom()
		{
			return from;
		}

		public int getTo()
		{
			return to;
		}
	}
}
