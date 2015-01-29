package ch.shaktipat.saraswati.internal.web.view.peventtopic;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.peventtopic.PEventTopicDetailController;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PEventTopicDetailPage extends Page
{

	public PEventTopicDetailPage()
	{
		super( new PEventTopicDetailController() );
	}

	@Override
	protected Content getContent()
	{
		return new PEventTopicDetailPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new PEventTopicDetailMenu( getController() );
	}
}
