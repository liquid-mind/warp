package ch.shaktipat.saraswati.internal.test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class TestStage
{
	private String name;
	private WriteLock writeLock;
	private Condition condition;
	private boolean testStageReached;
	
	public TestStage( String name )
	{
		super();
		this.name = name;
		writeLock = new ReentrantReadWriteLock().writeLock();
		condition = writeLock.newCondition();
		testStageReached = false;
	}
	
	public void signal()
	{
		writeLock.lock();
		
		try
		{
			if ( testStageReached )
				throw new IllegalStateException( "Thread tried to signal a test stage that was already reached: name=" + name );
			
			testStageReached = true;
			condition.signal();
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void await()
	{
		writeLock.lock();
		
		try
		{
			if ( !testStageReached )
				condition.awaitUninterruptibly();
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean testStageReached()
	{
		return testStageReached;
	}

	public String getName()
	{
		return name;
	}
}
