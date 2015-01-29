package ch.shaktipat.saraswati.internal.instrument;

import javassist.bytecode.ConstPool;

// Unfortunately, the ConstPool methods for adding items, i.e., addStringInfo()
// getIntegerInfo(), etc. are not smart enough to check whether an item
// has already been added or not; thus, the helper methods here are necessary.
public class ConstPoolHelper
{
	public static int getStringInfo( ConstPool constPool, String stringValue )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_String ) && ( constPool.getStringInfo( i ).equals( stringValue ) ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addStringInfo( stringValue );
		}

		return cpIndex;
	}

	public static int getIntegerInfo( ConstPool constPool, int intValue )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Integer ) && ( constPool.getIntegerInfo( i ) == intValue ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addIntegerInfo( intValue );
		}

		return cpIndex;
	}

	public static int getDoubleInfo( ConstPool constPool, double doubleValue )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Double ) && ( constPool.getDoubleInfo( i ) == doubleValue ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addDoubleInfo( doubleValue );
		}

		return cpIndex;
	}

	public static int getFloatInfo( ConstPool constPool, float floatValue )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Float ) && ( constPool.getFloatInfo( i ) == floatValue ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addFloatInfo( floatValue );
		}

		return cpIndex;
	}

	public static int getUtf8Info( ConstPool constPool, String utf8 )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Utf8 ) && constPool.getUtf8Info( i ).equals( utf8 ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addUtf8Info( utf8 );
		}

		return cpIndex;
	}

	public static int getClassInfo( ConstPool constPool, String className )
	{
		int cpIndex = -1;

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Class ) && constPool.getClassInfo( i ).equals( className ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addClassInfo( className );
		}

		return cpIndex;
	}

	public static int getNameAndType( ConstPool constPool, String name, String type )
	{
		int cpIndex = -1;

		int nameIndex = getUtf8Info( constPool, name );
		int typeIndex = getUtf8Info( constPool, type );

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_NameAndType ) && ( constPool.getNameAndTypeName( i ) == nameIndex ) && ( constPool.getNameAndTypeDescriptor( i ) == typeIndex ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addNameAndTypeInfo( nameIndex, typeIndex );
		}

		return cpIndex;
	}

	public static int getMethodRefInfo( ConstPool constPool, String className, String methodName, String methodDescriptor )
	{
		int cpIndex = -1;

		int classNameIndex = getClassInfo( constPool, className );
		int nameAndTypeIndex = getNameAndType( constPool, methodName, methodDescriptor );

		for ( int i = 1; i < constPool.getSize(); ++i )
		{
			if ( ( constPool.getTag( i ) == ConstPool.CONST_Methodref ) && ( constPool.getMethodrefClass( i ) == classNameIndex ) && ( constPool.getMethodrefNameAndType( i ) == nameAndTypeIndex ) )
			{
				cpIndex = i;
				break;
			}
		}

		if ( cpIndex == -1 )
		{
			cpIndex = constPool.addMethodrefInfo( classNameIndex, nameAndTypeIndex );
		}

		return cpIndex;
	}
}
