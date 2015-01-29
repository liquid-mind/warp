package ch.shaktipat.saraswati.internal.instrument.method;

import ch.shaktipat.saraswati.internal.common.FieldAndMethodConstants;
import ch.shaktipat.saraswati.internal.jvm.grammar.FQJVMMethodName;


public class MethodInvocation
{
	private int index;
	private FQJVMMethodName fqMethodName;
	private int lineNumber;
	private MethodInvocationType methodInvocationType;

	public MethodInvocation()
	{
		super();
	}

	public MethodInvocation( int index, FQJVMMethodName fqMethodName, int lineNumber, MethodInvocationType methodInvocationType )
	{
		super();
		this.index = index;
		this.fqMethodName = fqMethodName;
		this.lineNumber = lineNumber;
		this.methodInvocationType = methodInvocationType;
	}

	public boolean isObjectConstruction()
	{
		return fqMethodName.getJVMMethodName().equals( FieldAndMethodConstants.SARASWATI_NEW_MARKER_METHOD );
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex( int index )
	{
		this.index = index;
	}

	public FQJVMMethodName getFQMethodName()
	{
		return fqMethodName;
	}

	public void setFQMethodName( FQJVMMethodName fqMethodName )
	{
		this.fqMethodName = fqMethodName;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public void setLineNumber( int lineNumber )
	{
		this.lineNumber = lineNumber;
	}

	public MethodInvocationType getMethodInvocationType()
	{
		return methodInvocationType;
	}

	public void setMethodInvocationType( MethodInvocationType methodInvocationType )
	{
		this.methodInvocationType = methodInvocationType;
	}

}
