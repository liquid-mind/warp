package ch.shaktipat.saraswati.internal.instrument.method;

import javassist.bytecode.Bytecode;

public class BytecodeInsertion
{
	private int insertionIndex;
	private int gotoId;
	private Bytecode bytecode;

	public BytecodeInsertion()
	{
		super();
	}

	public BytecodeInsertion( int insertionIndex, int gotoId, Bytecode bytecode )
	{
		super();
		this.insertionIndex = insertionIndex;
		this.gotoId = gotoId;
		this.bytecode = bytecode;
	}

	public int getInsertionIndex()
	{
		return insertionIndex;
	}

	public void setInsertionIndex( int insertionIndex )
	{
		this.insertionIndex = insertionIndex;
	}

	public Bytecode getBytecode()
	{
		return bytecode;
	}

	public void setBytecode( Bytecode bytecode )
	{
		this.bytecode = bytecode;
	}

	public int getGotoId()
	{
		return gotoId;
	}

	public void setGotoId( int gotoId )
	{
		this.gotoId = gotoId;
	}
}
