package ch.shaktipat.saraswati.test.pobject.psharedobject;


public class SharedObjectImpl implements SharedObject
{
	private static final long serialVersionUID = 1L;

	@Override
	public String testMethod( String msg )
	{
		return msg + " pollo!";
	}
}
