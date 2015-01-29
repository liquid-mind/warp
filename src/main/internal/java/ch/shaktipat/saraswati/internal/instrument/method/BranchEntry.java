package ch.shaktipat.saraswati.internal.instrument.method;

public class BranchEntry
{
	// Points to the index (bytecode) just after the generated return
	// statement:
	// -initially set in the generator
	// -adjusted based on how much bytecode precedes the instrumented invocation
	// -used by replaceJumps() to determine which index to jump to when
	// continuing an instrumented invocation
	private int branchTargetIndex;

	// Points to the actual branch opcode (IF_ICMPEQ) in the preamble
	// that jumps to branchTargetIndex:
	// -set in preamble
	// -used by replaceJumps() to determine which index to jump to when
	// continuing an instrumented invocation
	private int branchInstructionIndex;

	// Points to the opcode in the method "invocation" instrumentation
	// that loads the pc constant from the constant table:
	// -initially set in the generator
	// -adjusted based on how much bytecode precedes the instrumented invocation
	// -used by replaceJumps() to set the actual pc constant, once it is known
	// (note that the pc constant is actually the branchTargetIndex)
	private int setPcInstructionIndex;

	// Points to the opcode in the preamble that loads the pc constant from
	// the constant table:
	// -initially set in the preamble
	// -used by replaceJumps() to set the actual pc constant, once it is known
	private int getPcInstructionIndex;

	// Points to the ATHROW opcode in the method "return" instrumentation:
	// -initially set in the generator
	// -adjusted based on how much bytecode precedes the instrumented invocation
	// -used by adjustExceptionTable() to to break up any
	// exception blocks into "instrumented" versions.
	private int thrownExceptionIndex;

	// Points to the first opcode of an instrumented invocation block:
	// -initially set by the generator
	// -adjusted based on how much bytecode precedes the invocation
	// -used by adjustExceptionTable() to determine whether the invocation
	// occurs within one or more exception blocks and to break up any
	// such blocks into "instrumented" versions.
	private int beginInsertionIndex;

	// Points to the opcode just after the last byte of an instrumented
	// invocation block:
	// -initially set by the generator
	// -adjusted based on how much bytecode precedes the invocation
	// -used by adjustExceptionTable() to to break up any
	// exception blocks into "instrumented" versions.
	private int endInsertionIndex;

	public BranchEntry()
	{
		super();
	}

	public BranchEntry(
			int gotoId,
			int branchTargetIndex,
			int branchInstructionIndex,
			int setPcInstructionIndex,
			int getPcInstructionIndex,
			int thrownExceptionIndex,
			int beginInsertionIndex,
			int endInsertionIndex )
	{
		super();
		this.branchTargetIndex = branchTargetIndex;
		this.branchInstructionIndex = branchInstructionIndex;
		this.setPcInstructionIndex = setPcInstructionIndex;
		this.getPcInstructionIndex = getPcInstructionIndex;
		this.thrownExceptionIndex = thrownExceptionIndex;
		this.beginInsertionIndex = beginInsertionIndex;
		this.endInsertionIndex = endInsertionIndex;
	}

	public int getBranchTargetIndex()
	{
		return branchTargetIndex;
	}

	public void setBranchTargetIndex( int branchTargetIndex )
	{
		this.branchTargetIndex = branchTargetIndex;
	}

	public int getBranchInstructionIndex()
	{
		return branchInstructionIndex;
	}

	public void setBranchInstructionIndex( int branchInstructionIndex )
	{
		this.branchInstructionIndex = branchInstructionIndex;
	}

	public int getSetPcInstructionIndex()
	{
		return setPcInstructionIndex;
	}

	public void setSetPcInstructionIndex( int setPcInstructionIndex )
	{
		this.setPcInstructionIndex = setPcInstructionIndex;
	}

	public int getGetPcInstructionIndex()
	{
		return getPcInstructionIndex;
	}

	public void setGetPcInstructionIndex( int getPcInstructionIndex )
	{
		this.getPcInstructionIndex = getPcInstructionIndex;
	}

	public int getThrownExceptionIndex()
	{
		return thrownExceptionIndex;
	}

	public void setThrownExceptionIndex( int thrownExceptionIndex )
	{
		this.thrownExceptionIndex = thrownExceptionIndex;
	}

	public int getBeginInsertionIndex()
	{
		return beginInsertionIndex;
	}

	public void setBeginInsertionIndex( int beginInsertionIndex )
	{
		this.beginInsertionIndex = beginInsertionIndex;
	}

	public int getEndInsertionIndex()
	{
		return endInsertionIndex;
	}

	public void setEndInsertionIndex( int endInsertionIndex )
	{
		this.endInsertionIndex = endInsertionIndex;
	}
}
