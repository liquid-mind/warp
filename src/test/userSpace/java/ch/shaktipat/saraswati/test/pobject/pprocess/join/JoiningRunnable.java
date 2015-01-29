package ch.shaktipat.saraswati.test.pobject.pprocess.join;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcessHandle;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;

@PersistentClass
public class JoiningRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void run()
	{
		Runnable runnable = new JoinableRunnable();
		PProcessHandle handle = PEnvironmentFactory.createLocal().getPProcessManager().create( runnable );
		handle.start();
		handle.join();
		handle.destroy();
		
		String success = (String)InterprocessCommunicationHelper.getProperty( handle.getOID(), JoinUserSpaceTest.JOIN_TEST_RESULT );
		InterprocessCommunicationHelper.setProperty( JoinUserSpaceTest.JOIN_TEST_RESULT, success );
	}
}
