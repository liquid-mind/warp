package ch.shaktipat.saraswati.internal.web.view.pprocess;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessDetailController;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PProcessDetailPage extends Page
{
	public PProcessDetailPage()
	{
		super( new PProcessDetailController() );
	}

	@Override
	protected Content getContent()
	{
		return new PProcessDetailPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new PProcessDetailMenu( getController() );
	}
}
