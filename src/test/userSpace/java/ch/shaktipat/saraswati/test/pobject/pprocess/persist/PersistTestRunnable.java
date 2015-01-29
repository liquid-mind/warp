package ch.shaktipat.saraswati.test.pobject.pprocess.persist;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PProcess;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;

@PersistentClass
public class PersistTestRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String msg;

	public PersistTestRunnable( String msg )
	{
		super();
		this.msg = msg;
	}

	@Override
	public void run()
	{
		PProcess thisProcess = PEnvironmentFactory.createLocal().getPProcessManager().getCurrentProcess();
		PersistTestEvent event = (PersistTestEvent)thisProcess.listen();
		String fullMsg = msg + event.getMsg();
		InterprocessCommunicationHelper.setProperty( PersistUserSpaceTest.PERSIST_TEST_RESULT, fullMsg );
	}
}
