package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class Link extends View
{
	private String value;
	private String link;
	
	public Link( Controller controller, String value, String link )
	{
		super( controller );
		this.value = value;
		this.link = link;
	}

	@Override
	public void render()
	{
		Writer.write( "<a href='" + link + "'>" + value + "</a>" );
	}
}
