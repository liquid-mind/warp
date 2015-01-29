package ch.shaktipat.saraswati.internal.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import ch.shaktipat.exwrapper.java.util.concurrent.ExecutorServiceWrapper;
import ch.shaktipat.saraswati.internal.classloading.PClassLoader;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPool;
import ch.shaktipat.saraswati.internal.pool.OmniObjectPoolImpl;
import ch.shaktipat.saraswati.internal.scheduler.PersistentEventScheduler;

public class PEngine
{
	private static Logger logger = Logger.getLogger( PEngine.class.getName() );
	
	// Note that for debugging I am setting a fairly high wait interval of
	// 60 minutues (60*60) to prevent the awaitTermination() method from
	// returning while debugging is still underway.
	private static final long THREAD_TERMINATION_WAIT_INTERVAL = 60 * 60;

	private static PEngine pEngineInstance;
	
	private PClassLoader pClassLoader;
	private OmniObjectPool omniObjectPool;
	private PersistentEventScheduler persistentScheduler;
	private ExecutorService executorService;
	private SaraswatiConfiguration saraswatiConfiguration;
	
	static
	{
		// This only needs to be done once, so am doing it here.
		Runtime.getRuntime().addShutdownHook( new ShutdownThread() );
	}
	
	public PEngine()
	{
		super();
	}
	
	public void start()
	{
		saraswatiConfiguration = new SaraswatiConfiguration();
		saraswatiConfiguration.start();
		pClassLoader = new PClassLoader();
		omniObjectPool = new OmniObjectPoolImpl();
		omniObjectPool.start();
		persistentScheduler = new PersistentEventScheduler( omniObjectPool );
		persistentScheduler.start( omniObjectPool );
		executorService = Executors.newFixedThreadPool( saraswatiConfiguration.getPObjectsThreadPoolSize(), new SaraswatiThreadFactory() );
	}
	
	public void stop()
	{
		executorService.shutdown();
		ExecutorServiceWrapper.awaitTermination( executorService, THREAD_TERMINATION_WAIT_INTERVAL, TimeUnit.SECONDS );
		executorService.shutdownNow();
		
		if ( !executorService.isTerminated() )
			logger.warning( "Was not able to fully stop thread pool; may not be able to reliably stop other services." );
		
		persistentScheduler.stop();
		omniObjectPool.stop();
		saraswatiConfiguration.stop();
	}

	public static synchronized void startup()
	{
		// Must already be running --> do nothing.
		if ( pEngineInstance != null )
			return;
		
		logger.info( "Starting engine." );
		pEngineInstance = new PEngine();
		pEngineInstance.start();
	}
	
	public static synchronized void shutdown()
	{
		// Must already be shutdown --> do nothing.
		if ( pEngineInstance == null )
			return;
		
		logger.info( "Stopping engine." );
		pEngineInstance.stop();
		pEngineInstance = null;
	}
	
	public static PEngine getPEngine()
	{
		if ( pEngineInstance == null )
			PEngine.startup();
		
		Thread.currentThread().setContextClassLoader( pEngineInstance.getPClassLoader() );
		
		return pEngineInstance;
	}

	public OmniObjectPool getOmniObjectPool()
	{
		return omniObjectPool;
	}

	public PClassLoader getPClassLoader()
	{
		return pClassLoader;
	}

	public ExecutorService getExecutorService()
	{
		return executorService;
	}

	public PersistentEventScheduler getPersistentScheduler()
	{
		return persistentScheduler;
	}

	public SaraswatiConfiguration getSaraswatiConfiguration()
	{
		return saraswatiConfiguration;
	}
}
