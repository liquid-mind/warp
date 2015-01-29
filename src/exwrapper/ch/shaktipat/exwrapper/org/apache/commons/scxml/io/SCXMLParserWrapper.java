package ch.shaktipat.exwrapper.org.apache.commons.scxml.io;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import ch.shaktipat.exwrapper.java.io.IOExceptionWrapper;
import ch.shaktipat.exwrapper.org.apache.commons.scxml.model.ModelExceptionWrapper;
import ch.shaktipat.exwrapper.org.xml.sax.SAXExceptionWrapper;

public class SCXMLParserWrapper
{
	public static SCXML parse( URL url, ErrorHandler errHandler ) 
	{
		try
		{
			return SCXMLParser.parse( url, errHandler );
		}
		catch ( ModelException e )
		{
			throw new ModelExceptionWrapper( e );
		}
		catch ( SAXException e )
		{
			throw new SAXExceptionWrapper( e );
		}
		catch ( IOException e )
		{
			throw new IOExceptionWrapper( e );
		}
	}
}
