package ch.shaktipat.saraswati.test.common;

import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.pprocess.PProcessExecutionSegmentResult;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public abstract class AbstractPersistentInvocationBaseTest
{
	private String testClassName;

	protected AbstractPersistentInvocationBaseTest( String testClassName )
	{
		super();
		this.testClassName = testClassName;
	}

	protected Object invokePersistentMethod( String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		return invokePersistentMethod( Volatility.VOLATILE, methodName, paramTypes, params );
	}

	protected Object invokePersistentMethod( Volatility volatility, String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		PProcessHandle pProcessHandle = PEnvironmentFactory.createLocal().getPProcessManager().create();
		PProcessExecutionSegmentResult result = invokePersistentMethodWithBorrowedObject( pProcessHandle.getOID(), volatility, methodName, paramTypes, params );
		pProcessHandle.destroy();
		
		if ( result.getException() != null )
			throw result.getException();
		
		return result.getReturnValue();
	}

	private PProcessExecutionSegmentResult invokePersistentMethodWithBorrowedObject( PProcessOID pProcessOID, Volatility volatility, String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		OmniObjectPool pool = PEngine.getPEngine().getOmniObjectPool();
		PersistentProcess pProcess = (PersistentProcess)pool.borrowPersistentObject( pProcessOID, false );
		PProcessExecutionSegmentResult result = null;
		
		try
		{
			invokePersistentMethodWithCurrentProcessOID( pProcess, volatility, methodName, paramTypes, params );
			result = pProcess.getpProcessExecutionSegmentResult();
		}
		finally
		{
			pool.returnPersistentObject( pProcess, false );
		}
		
		return result;
	}
	
	private void invokePersistentMethodWithCurrentProcessOID( PersistentProcess pProcess, Volatility volatility, String methodName, Class< ? >[] paramTypes, Object[] params ) throws Throwable
	{
		PersistentProcessImpl.setCurrentProcessOID( pProcess.getOID() );
		
		try
		{
			pProcess.invokeTestMethod( Volatility.VOLATILE, testClassName, methodName, paramTypes, params );
		}
		finally
		{
			PersistentProcessImpl.setCurrentProcessOID( null );
		}
	}
}
