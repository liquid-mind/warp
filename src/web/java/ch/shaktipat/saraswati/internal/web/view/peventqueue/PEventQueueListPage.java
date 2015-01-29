package ch.shaktipat.saraswati.internal.web.view.peventqueue;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.peventqueue.PEventQueueListController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PEventQueueListPage extends Page
{

	public PEventQueueListPage()
	{
		super( new PEventQueueListController() );
	}

	@Override
	protected Content getContent()
	{
		return new PEventQueueListPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}
}
