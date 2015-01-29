package ch.shaktipat.saraswati.internal.web.view.peventqueue;

import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailMenu;

public class PEventQueueDetailMenu extends POwnableObjectDetailMenu
{
	public PEventQueueDetailMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected String getConfirmDestroyMessage()
	{
		return "You are about to destroy this event queue; this cannot be undone. Do you want to continue?";
	}

	@Override
	protected Class< ? > getCommandTargetClass()
	{
		return PEventQueueDetailPage.class;
	}

	@Override
	protected String getToolTipText()
	{
		return "destroy event queue";
	}
}
