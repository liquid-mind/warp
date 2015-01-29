package ch.shaktipat.saraswati.internal.web.view.pprocess;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessStateMachineController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PProcessStateMachinePage extends Page
{
	public PProcessStateMachinePage()
	{
		super( new PProcessStateMachineController() );
	}

	@Override
	protected Content getContent()
	{
		return new PProcessStateMachinePageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}
}
