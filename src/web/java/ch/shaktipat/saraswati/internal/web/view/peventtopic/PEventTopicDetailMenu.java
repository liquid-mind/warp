package ch.shaktipat.saraswati.internal.web.view.peventtopic;

import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailMenu;

public class PEventTopicDetailMenu extends POwnableObjectDetailMenu
{
	public PEventTopicDetailMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected String getConfirmDestroyMessage()
	{
		return "You are about to destroy this event topic; this cannot be undone. Do you want to continue?";
	}

	@Override
	protected Class< ? > getCommandTargetClass()
	{
		return PEventTopicDetailPage.class;
	}

	@Override
	protected String getToolTipText()
	{
		return "destroy event topic";
	}
}
