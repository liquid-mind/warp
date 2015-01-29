package ch.shaktipat.saraswati.internal.web.view.peventtopic;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.peventtopic.PEventTopicListController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PEventTopicListPage extends Page
{
	public PEventTopicListPage()
	{
		super( new PEventTopicListController() );
	}

	@Override
	protected Content getContent()
	{
		return new PEventTopicListPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}
}
