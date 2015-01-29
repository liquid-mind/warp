package ch.shaktipat.saraswati.internal.web.view.psharedobject;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.psharedobject.PSharedObjectListController;
import ch.shaktipat.saraswati.internal.web.view.BasicMenu;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PSharedObjectListPage extends Page
{
	public PSharedObjectListPage()
	{
		super( new PSharedObjectListController() );
	}

	@Override
	protected Content getContent()
	{
		return new PSharedObjectListPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new BasicMenu( getController() );
	}
}
