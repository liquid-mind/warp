package ch.shaktipat.saraswati.internal.pobject.aux.pprocess;

import java.io.Serializable;


public class PProcessExecutionSegment implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private PFrameStack pFrameStack = new PFrameStack();
	private PProcessPMethodCommunicationArea pProcessPMethodCommunicationArea = new PProcessPMethodCommunicationArea();
	private MethodInvocationInstance nextMethodInvocationInstance;
	private MethodResultInstance lastMethodResultInstance;
	
	public PProcessExecutionSegment(
			PFrameStack pFrameStack,
			PProcessPMethodCommunicationArea pProcessPMethodCommunicationArea,
			MethodInvocationInstance nextMethodInvocationInstance,
			MethodResultInstance lastMethodResultInstance )
	{
		super();
		this.pFrameStack = pFrameStack;
		this.pProcessPMethodCommunicationArea = pProcessPMethodCommunicationArea;
		this.nextMethodInvocationInstance = nextMethodInvocationInstance;
		this.lastMethodResultInstance = lastMethodResultInstance;
	}

	public PFrameStack getPFrameStack()
	{
		return pFrameStack;
	}

	public void setpFrameStack( PFrameStack pFrameStack )
	{
		this.pFrameStack = pFrameStack;
	}

	public PProcessPMethodCommunicationArea getPProcessPMethodCommunicationArea()
	{
		return pProcessPMethodCommunicationArea;
	}

	public void setpProcessPMethodCommunicationArea( PProcessPMethodCommunicationArea pProcessPMethodCommunicationArea )
	{
		this.pProcessPMethodCommunicationArea = pProcessPMethodCommunicationArea;
	}

	public MethodInvocationInstance getNextMethodInvocationInstance()
	{
		return nextMethodInvocationInstance;
	}

	public void setNextMethodInvocationInstance( MethodInvocationInstance nextMethodInvocationInstance )
	{
		this.nextMethodInvocationInstance = nextMethodInvocationInstance;
	}

	public MethodResultInstance getLastMethodResultInstance()
	{
		return lastMethodResultInstance;
	}

	public void setLastMethodResultInstance( MethodResultInstance lastMethodResultInstance )
	{
		this.lastMethodResultInstance = lastMethodResultInstance;
	}
}
