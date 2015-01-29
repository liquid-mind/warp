package ch.shaktipat.saraswati.internal.web.view.psharedobject;

import ch.shaktipat.saraswati.internal.web.annotations.SaraswatiPage;
import ch.shaktipat.saraswati.internal.web.controller.psharedobject.PSharedObjectDetailController;
import ch.shaktipat.saraswati.internal.web.view.Content;
import ch.shaktipat.saraswati.internal.web.view.Menu;
import ch.shaktipat.saraswati.internal.web.view.Page;

@SaraswatiPage
public class PSharedObjectDetailPage extends Page
{
	public PSharedObjectDetailPage()
	{
		super( new PSharedObjectDetailController() );
	}

	@Override
	protected Content getContent()
	{
		return new PSharedObjectDetailPageContent( getController() );
	}

	@Override
	protected Menu getMenu()
	{
		return new PSharedObjectDetailMenu( getController() );
	}
}
