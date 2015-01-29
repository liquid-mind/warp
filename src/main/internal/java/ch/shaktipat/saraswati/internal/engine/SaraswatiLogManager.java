package ch.shaktipat.saraswati.internal.engine;

import java.util.logging.LogManager;

public class SaraswatiLogManager extends LogManager
{
    @Override
    public void reset() {}

	public void invokeSuperReset()
    {
    	super.reset();
    }
    
    public static void sarswatiReset()
    {
    	SaraswatiLogManager manager = (SaraswatiLogManager)SaraswatiLogManager.getLogManager();
    	manager.invokeSuperReset();
    }

    // TODO Logging is fairly broken at this point; needs to be fixed. May need
    // to move to log4j...
//	@Override
//	public Logger getLogger( String name )
//	{
//		Logger logger = null;
//		
//		if ( name.contains( PersistentProcessImpl.PPROCESS_SYSTEM_LOGGER ) || name.contains( PersistentProcessImpl.PPROCESS_APPLICATION_LOGGER ) )
//			logger = new SaraswatiLogger( name, null );
//		else
//			logger = super.getLogger( name );
//		
//		return logger;
//	}
}
