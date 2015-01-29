package ch.shaktipat.saraswati.internal.instrument.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javassist.bytecode.ExceptionTable;

public class ExceptionTableEntry
{
	private int startPc;
	private int endPc;
	private int handlerPc;
	private int catchType;
	
	public ExceptionTableEntry( int startPc, int endPc, int handlerPc, int catchType )
	{
		super();
		this.startPc = startPc;
		this.endPc = endPc;
		this.handlerPc = handlerPc;
		this.catchType = catchType;
	}
	
	public static List< ExceptionTableEntry > convert( ExceptionTable exceptionTable )
	{
		List< ExceptionTableEntry > exceptionTableEntries = new ArrayList< ExceptionTableEntry >();
		
		for ( int i = 0; i < exceptionTable.size(); ++i )
		{
			exceptionTableEntries.add( new ExceptionTableEntry(
				exceptionTable.startPc( i ),
				exceptionTable.endPc( i ),
				exceptionTable.handlerPc( i ),
				exceptionTable.catchType( i ) ) );
		}
		
		return exceptionTableEntries;
	}
	
	public static void replaceExceptionTable( ExceptionTable exceptionTable, List< ExceptionTableEntry > exceptionTableEntries )
	{
		// Note: unfortunately there is no set method to correspond
		// to the CodeAttribute.getExceptionTable() method. That's why
		// I am doing this replacement in such a clunky way.
		while ( exceptionTable.size() > 0 )
			exceptionTable.remove( 0 );

		for ( ExceptionTableEntry entry : exceptionTableEntries )
			exceptionTable.add( entry.getStartPc(), entry.getEndPc(), entry.getHandlerPc(), entry.getCatchType() );
	}
	
	public static void sort( List< ExceptionTableEntry > exceptionTableEntries )
	{
		Comparator< ExceptionTableEntry > comparator = new Comparator< ExceptionTableEntry >() {
			@Override
			public int compare( ExceptionTableEntry o1, ExceptionTableEntry o2 )
			{
				return o1.getStartPc() - o2.getStartPc();
			}
		};
		
		Collections.sort( exceptionTableEntries, comparator );
	}

	public int getStartPc()
	{
		return startPc;
	}

	public void setStartPc( int startPc )
	{
		this.startPc = startPc;
	}

	public int getEndPc()
	{
		return endPc;
	}

	public void setEndPc( int endPc )
	{
		this.endPc = endPc;
	}

	public int getHandlerPc()
	{
		return handlerPc;
	}

	public void setHandlerPc( int handlerPc )
	{
		this.handlerPc = handlerPc;
	}

	public int getCatchType()
	{
		return catchType;
	}

	public void setCatchType( int catchType )
	{
		this.catchType = catchType;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + catchType;
		result = prime * result + endPc;
		result = prime * result + handlerPc;
		result = prime * result + startPc;
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		ExceptionTableEntry other = (ExceptionTableEntry)obj;
		if ( catchType != other.catchType )
			return false;
		if ( endPc != other.endPc )
			return false;
		if ( handlerPc != other.handlerPc )
			return false;
		if ( startPc != other.startPc )
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ExceptionTableEntry [startPc=" + startPc + ", endPc=" + endPc + ", handlerPc=" + handlerPc + ", catchType=" + catchType + "]";
	}
}
