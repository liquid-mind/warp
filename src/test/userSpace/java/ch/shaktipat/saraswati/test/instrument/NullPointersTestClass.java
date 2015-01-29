package ch.shaktipat.saraswati.test.instrument;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;

@PersistentClass
public class NullPointersTestClass implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public void testNullInLocalVariable()
	{
		// Note: setting s = null creates a situation
		// where a null reference must be copied to the virtual
		// frame and then back again.
		// Internally, this causes BytecodeGenerator.copyLocalVariableToVFrame()
		// to by invoked with the type set to UNINIT (see javassist javadoc)
		@SuppressWarnings( "unused" )
		String s = null;
		doNothingMethod();
	}

	public void doNothingMethod()
	{
	}

	public void testNullInParameter()
	{
		// Here we are causing a null reference to be put on the
		// operand stack *before* invocation.
		// Internally, this causes BytecodeGenerator.popTopOfStackToVFrame()
		// to by invoked with the type set to UNINIT
		doReceiveNullRefMethod( null );
	}

	public void doReceiveNullRefMethod( String s )
	{
	}

	public void testNullInReturn()
	{
		// Here we are causing a null reference to be put on the
		// operand stack *after* invocation.
		// Internally, this causes BytecodeGenerator.pushVFrameItemOntoStack()
		// to by invoked with the type set to java.lang.String
		String value = doReturnNullRefMethod();
		
		if ( value != null )
			throw new RuntimeException( "value != null" );
	}

	public String doReturnNullRefMethod()
	{
		return null;
	}
	
	@SuppressWarnings( "null" )
	public boolean testNullPointerException()
	{
		boolean success = false;
		String s = null;
		
		try
		{
			// Provoke a NullPointerException by dereferencing a null pointer.
			// Note that from the engine's perspecive, this does not have the
			// same effect as throwing a NullPointerException; in the former
			// case, the NullPointerException is caused by the engine itself
			// (when trying to call Method.invoke()) whereas in the later case
			// the exception is caused by the method and caught in the engine
			// as a InvocationTargetException.
			s.length();
		}
		catch ( NullPointerException e )
		{
			success = true;
		}
		
		return success;
	}
}
