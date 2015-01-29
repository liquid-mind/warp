package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class Header extends View
{
	public Header( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		Writer.write( "<div class='title'>SARASWATI</div>" );
		Writer.write( "<div class='subtitle'>Administration Console</div>" );
	}
}
