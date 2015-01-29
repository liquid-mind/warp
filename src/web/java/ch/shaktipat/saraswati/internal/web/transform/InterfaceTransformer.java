package ch.shaktipat.saraswati.internal.web.transform;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import ch.shaktipat.saraswati.internal.web.annotations.TransformerMarker;

@TransformerMarker
public class InterfaceTransformer implements Transformer< Class< ? > >
{
	@Override
	public String getString( Class< ? > theClass )
	{
		String classAsString = "";
		classAsString += "public interface " + theClass.getName() + "\n";
		classAsString += "{\n";
		
		for ( Method method : theClass.getDeclaredMethods() )
		{
			classAsString += "\tpublic " + method.getReturnType().getName() + " " + method.getName() + "(" + getParameters( method ) + ")";
			classAsString += getExceptions( method ) + ";\n";
		}
		
		classAsString += "}\n";
		
		return classAsString;
	}

	private static String getParameters( Method method )
	{
		String parameters = "";
		
		List< String > paramTypesAndNames = new ArrayList< String >();
		int argCounter = 0;
		
		for ( Class< ? > paramType : method.getParameterTypes() )
			paramTypesAndNames.add( paramType.getName() + " arg" + argCounter++ );
		
		if ( paramTypesAndNames.size() > 0 )
			parameters += " " + StringUtils.join( paramTypesAndNames, ", " ) + " ";
		
		return parameters;
	}

	private static String getExceptions( Method method )
	{
		String exceptions = "";
		
		List< String > thrownExceptionTypes = new ArrayList< String >();
		
		for ( Class< ? > exceptionType : method.getExceptionTypes() )
			thrownExceptionTypes.add( exceptionType.getName() );
		
		if ( thrownExceptionTypes.size() > 0 )
			exceptions += " throws " + StringUtils.join( thrownExceptionTypes, ", " );
		
		return exceptions;
	}
	
	@Override
	public Class< ? > getValue( String pProcessOIDjAsString )
	{
		throw new UnsupportedOperationException();
	}
}
