package ch.shaktipat.saraswati.internal.web.view.pprocess;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.pprocess.PProcessListController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PProcessListPage extends Page
{
	public PProcessListPage()
	{
		super( new PProcessListController() );
	}

	@Override
	protected Content getContent()
	{
		return new PProcessListPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}
}
