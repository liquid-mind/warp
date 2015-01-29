package ch.shaktipat.saraswati.internal.web.view.server;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.server.ServerController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.EmptyTabs;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;
import ch.shaktipat.saraswati.internal.web.view.Tabs;

@SaraswatiPage
public class ServerPage extends Page
{
	public ServerPage()
	{
		super( new ServerController() );
	}

	@Override
	protected Content getContent()
	{
		return new ServerPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}

	@Override
	protected Tabs getTabs()
	{
		return new EmptyTabs( getController() );
	}
}
