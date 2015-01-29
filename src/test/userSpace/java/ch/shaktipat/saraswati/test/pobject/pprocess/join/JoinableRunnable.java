package ch.shaktipat.saraswati.test.pobject.pprocess.join;

import java.io.Serializable;

import ch.shaktipat.saraswati.common.PersistentClass;
import ch.shaktipat.saraswati.test.common.InterprocessCommunicationHelper;
import ch.shaktipat.saraswati.test.common.TimingHelper;

@PersistentClass
public class JoinableRunnable implements Runnable, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long waitPeriodMillis;
	
	public JoinableRunnable()
	{
		this( 0 );
	}

	public JoinableRunnable( long waitPeriodMillis )
	{
		super();
		this.waitPeriodMillis = waitPeriodMillis;
	}

	@Override
	public void run()
	{
		TimingHelper.sleep( waitPeriodMillis );
		InterprocessCommunicationHelper.setProperty( JoinUserSpaceTest.JOIN_TEST_RESULT, JoinUserSpaceTest.SUCCESS );
	}
}
