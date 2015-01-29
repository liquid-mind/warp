package ch.shaktipat.saraswati.internal.instrument.method;

import java.util.List;

public class GenerateCodeResult
{
	private List< BytecodeInsertion > bytecodeInsertions;
	private List< BranchEntry > branchEntries;

	public GenerateCodeResult()
	{
		super();
	}

	public GenerateCodeResult( List< BytecodeInsertion > bytecodeInsertions, List< BranchEntry > branchEntries )
	{
		super();
		this.bytecodeInsertions = bytecodeInsertions;
		this.branchEntries = branchEntries;
	}

	public List< BytecodeInsertion > getBytecodeInsertions()
	{
		return bytecodeInsertions;
	}

	public void setBytecodeInsertions( List< BytecodeInsertion > bytecodeInsertions )
	{
		this.bytecodeInsertions = bytecodeInsertions;
	}

	public List< BranchEntry > getBranchEntries()
	{
		return branchEntries;
	}

	public void setBranchEntries( List< BranchEntry > branchEntries )
	{
		this.branchEntries = branchEntries;
	}
}
