package ch.shaktipat.saraswati.internal.web.view;

import java.util.Arrays;
import java.util.List;

import ch.shaktipat.saraswati.internal.web.SaraswatiServlet;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;
import ch.shaktipat.saraswati.internal.web.view.peventqueue.PEventQueueDetailPage;
import ch.shaktipat.saraswati.internal.web.view.peventqueue.PEventQueueListPage;
import ch.shaktipat.saraswati.internal.web.view.peventtopic.PEventTopicDetailPage;
import ch.shaktipat.saraswati.internal.web.view.peventtopic.PEventTopicListPage;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessDetailPage;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessListPage;
import ch.shaktipat.saraswati.internal.web.view.pprocess.PProcessStateMachinePage;
import ch.shaktipat.saraswati.internal.web.view.psharedobject.PSharedObjectDetailPage;
import ch.shaktipat.saraswati.internal.web.view.psharedobject.PSharedObjectListPage;


public class Tabs extends View
{
	private Class< ? > pageClass;
	
	public Tabs( Controller controller, Class< ? > pageClass )
	{
		super( controller );
		this.pageClass = pageClass;
	}

	@Override
	public void render()
	{
		Writer.write( "<ul class='nav nav-tabs'>" );
		Writer.write( "<li class='" + getMenuItemClass( PProcessListPage.class, PProcessDetailPage.class, PProcessStateMachinePage.class ) + "'><a href='" + SaraswatiServlet.getPageURL( PProcessListPage.class ) + "'>Processes</a></li>" );
		Writer.write( "<li class='" + getMenuItemClass( PEventQueueListPage.class, PEventQueueDetailPage.class ) + "'><a href='" + SaraswatiServlet.getPageURL( PEventQueueListPage.class ) + "'>Event Queues</a></li>" );
		Writer.write( "<li class='" + getMenuItemClass( PEventTopicListPage.class, PEventTopicDetailPage.class ) + "'><a href='" + SaraswatiServlet.getPageURL( PEventTopicListPage.class ) + "'>Event Topics</a></li>" );
		Writer.write( "<li class='" + getMenuItemClass( PSharedObjectListPage.class, PSharedObjectDetailPage.class ) + "'><a href='" + SaraswatiServlet.getPageURL( PSharedObjectListPage.class ) + "'>Shared Objects</a></li>" );
		Writer.write( "</ul>" );
		Writer.write( "<br>" );
	}
	
	private String getMenuItemClass( Class< ? > ... itemPageClassesAsArray )
	{
		List< Class< ? > > itemPageClasses = Arrays.asList( itemPageClassesAsArray );
		String menuItemClass = "";
		
		if ( itemPageClasses.contains( pageClass ) )
			menuItemClass = "active";
		
		return menuItemClass;
	}
}
