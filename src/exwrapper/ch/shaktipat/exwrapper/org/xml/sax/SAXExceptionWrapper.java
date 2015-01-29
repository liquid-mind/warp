package ch.shaktipat.exwrapper.org.xml.sax;

import org.xml.sax.SAXException;

public class SAXExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public SAXExceptionWrapper( SAXException e )
	{
		super( e );
	}
}
