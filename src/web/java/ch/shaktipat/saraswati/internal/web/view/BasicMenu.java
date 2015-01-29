package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class BasicMenu extends Menu
{
	public BasicMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected void renderPageSpecificButtons() {}

	@Override
	protected void renderPageSpecificModals() {}
}
