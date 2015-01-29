package ch.shaktipat.saraswati.internal.web.view;

import java.util.Date;

import ch.shaktipat.saraswati.internal.web.WebConstants;
import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;

public class Footer extends View
{
	public Footer( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		String year = WebConstants.YEAR_ONLY_DATE_FORMAT.format( new Date() );
		
		Writer.write( "<div class='container'>" );
		Writer.write( "<hr>" );
		Writer.write( "<footer class='text-muted'>Copyright &copy; " + year + " Project Shaktipat</footer>" );
		Writer.write( "</div>" );
	}
}
