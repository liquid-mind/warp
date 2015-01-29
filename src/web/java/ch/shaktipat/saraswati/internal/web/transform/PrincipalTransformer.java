package ch.shaktipat.saraswati.internal.web.transform;

import java.security.Principal;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class PrincipalTransformer implements Transformer< Principal >
{
	@Override
	public String getString( Principal principal )
	{
		return principal.toString();
	}

	@Override
	public Principal getValue( String principalAsString )
	{
		// TODO Implement this once security has been integrated.
		throw new UnsupportedOperationException();
	}
}
