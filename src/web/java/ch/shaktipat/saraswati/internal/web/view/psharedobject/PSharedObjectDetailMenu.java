package ch.shaktipat.saraswati.internal.web.view.psharedobject;

import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.POwnableObjectDetailMenu;

public class PSharedObjectDetailMenu extends POwnableObjectDetailMenu
{
	public PSharedObjectDetailMenu( Controller controller )
	{
		super( controller );
	}

	@Override
	protected String getConfirmDestroyMessage()
	{
		return "You are about to destroy this shared object; this cannot be undone. Do you want to continue?";
	}

	@Override
	protected Class< ? > getCommandTargetClass()
	{
		return PSharedObjectDetailPage.class;
	}

	@Override
	protected String getToolTipText()
	{
		return "destroy shared object";
	}
}
