package ch.shaktipat.saraswati.internal.web.transform;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class ClassTransformer implements Transformer< Class< ? > >
{
	@Override
	public String getString( Class< ? > theClass )
	{
		String classAsString = null;
		
		if ( theClass != null )
			classAsString = theClass.getName();
		
		return classAsString;
	}

	@Override
	public Class< ? > getValue( String pProcessOIDjAsString )
	{
		throw new UnsupportedOperationException();
	}
}
