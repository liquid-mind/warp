package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class EmptyTabs extends Tabs
{
	public EmptyTabs( Controller controller )
	{
		super( controller, null );
	}

	@Override
	public void render()
	{
	}
}
