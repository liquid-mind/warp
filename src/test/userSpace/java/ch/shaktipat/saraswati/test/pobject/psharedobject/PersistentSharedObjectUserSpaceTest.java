package ch.shaktipat.saraswati.test.pobject.psharedobject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ch.shaktipat.saraswati.environment.PEnvironmentFactory;
import ch.shaktipat.saraswati.pobject.PSharedObjectHandle;

// TODO Add tests where the shared object is owned by a process; make
// sure its life-cycle is handled correctly.
public class PersistentSharedObjectUserSpaceTest
{
	public void test()
	{
		SharedObjectImpl sharedObjectImpl = new SharedObjectImpl();
		PSharedObjectHandle pSharedObjectHandle = PEnvironmentFactory.createLocal().getPSharedObjectManager().create( sharedObjectImpl );
		SharedObject sharedObject = pSharedObjectHandle.get();
		String msg = sharedObject.testMethod( "marco" );
		pSharedObjectHandle.destroy();
		
		assertNotNull( msg );
		assertEquals( "marco pollo!", msg );
	}
}
