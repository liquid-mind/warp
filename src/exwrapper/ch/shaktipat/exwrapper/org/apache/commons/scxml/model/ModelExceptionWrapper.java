package ch.shaktipat.exwrapper.org.apache.commons.scxml.model;

import org.apache.commons.scxml.model.ModelException;

public class ModelExceptionWrapper extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ModelExceptionWrapper( ModelException e )
	{
		super( e );
	}
}
