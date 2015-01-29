package ch.shaktipat.saraswati.internal.web.view.peventqueue;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.peventqueue.PEventQueueDetailController;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PEventQueueDetailPage extends Page
{

	public PEventQueueDetailPage()
	{
		super( new PEventQueueDetailController() );
	}

	@Override
	protected Content getContent()
	{
		return new PEventQueueDetailPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new PEventQueueDetailMenu( getController() );
	}
}
