package ch.shaktipat.saraswati.internal.web.view;

import ch.shaktipat.saraswati.internal.web.Writer;
import ch.shaktipat.saraswati.internal.web.controller.Controller;


public abstract class Page extends View
{
	public Page( Controller controller )
	{
		super( controller );
	}

	@Override
	public void render()
	{
		Writer.write( "<!DOCTYPE html>" );
		Writer.write( "<html lang='en'>" );
		Writer.write( "<head>" );
		Writer.write( "<meta charset='utf-8'>" );
		Writer.write( "<meta http-equiv='X-UA-Compatible' content='IE=edge'>" );
		Writer.write( "<meta name='viewport' content='width=device-width, initial-scale=1'>" );
		Writer.write( "<title>Saraswati Admin Console</title>" );
		
		Writer.write( "<!-- Bootstrap -->" );
		Writer.write( "<link href='css/bootstrap.min.css' rel='stylesheet'>" );
		Writer.write( "<link href='css/jasny-bootstrap.min.css' rel='stylesheet'>" );
		
		Writer.write( "<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->" );
		Writer.write( "<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->" );
		Writer.write( "<!--[if lt IE 9]>" );
		Writer.write( "<script src='https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js'></script>" );
		Writer.write( "<script src='https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js'></script>" );
		Writer.write( "<![endif]-->" );
		Writer.write( "</head>" );
		Writer.write( "<body>" );

		getMenu().render();
		
		Writer.write( "<div class='container'>" );
		
		getTabs().render();

		Writer.write( "<div>" );
		
		getContent().render();

		Writer.write( "</div>" );
		Writer.write( "</div>" );

		getFooter().render();
		
		Writer.write( "<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->" );
		Writer.write( "<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js'></script>" );
		Writer.write( "<!-- Include all compiled plugins (below), or include individual files as needed -->" );
		Writer.write( "<script src='js/bootstrap.min.js'></script>" );
		Writer.write( "<script src='js/jasny-bootstrap.min.js'></script>" );
		Writer.write( "<script src='/ch/shaktipat/saraswati/internal/web/ui/saraswati.js'></script>" );
		Writer.write( "</body>" );
		Writer.write( "</html>" );
	}
	
	protected Header getHeader()
	{
		return new Header( getController() );
	}
	
	protected Footer getFooter()
	{
		return new Footer( getController() );
	}

	protected abstract Menu getMenu();

	protected Tabs getTabs()
	{
		return new Tabs( getController(), this.getClass() );
	}
	
	protected abstract Content getContent();
}
