package ch.shaktipat.saraswati.internal.pool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ch.shaktipat.exwrapper.java.io.FileInputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.FileOutputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.InputStreamWrapper;
import ch.shaktipat.exwrapper.java.io.OutputStreamWrapper;
import ch.shaktipat.exwrapper.java.lang.ClassWrapper;
import ch.shaktipat.saraswati.internal.dynproxies.ProxyType;
import ch.shaktipat.saraswati.internal.engine.PEngine;
import ch.shaktipat.saraswati.internal.pobject.PObjectOIDFactory;
import ch.shaktipat.saraswati.internal.pobject.PersistentObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentObjectImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentOwnableObject;
import ch.shaktipat.saraswati.internal.pobject.PersistentOwnableObjectImpl;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcess;
import ch.shaktipat.saraswati.internal.pobject.PersistentProcessImpl;
import ch.shaktipat.saraswati.internal.pobject.aux.PersistentObjectVisitor;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.POwnableObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

// There are two operations (so far) that require exclusive access; not just
// to the stateLock, but to the PersistentObject instance itself. In other words,
// these operations require that no other clients are holding a java reference
// to the PersistentObject instance. The operations are passivation and destruction.
// The reason for this special form of exclusive access is that both operations
// cause the PersistentObject instance to be invalid. In the case of passivation, the
// valid PersistentObject is either in persistent storage or has been re-read into a new
// PersistentObject. In the case of destruction there simply is no more PersistentObject instance.
//
// For all other operations, i.e. starting, stopping, sending events, joining, etc.
// it is sufficient to have exclusive access to the object state without any
// guarantee as to which other clients might be referencing the same Java object.
// Also, for all other operations, a lock may be temporarily released, e.g. when
// invoking the await() or awaitUninterruptably() methods. In the case of
// passivation/destruction the lock must never be interrupted.

public class PersistentObjectPoolImpl implements PersistentObjectPool
{
	private static Logger logger = Logger.getLogger(  PersistentObjectPoolImpl.class.getName() );

	private String pObjectsDir;
	protected Map< PObjectOID, PersistentObject > persistentObjectsInMemory;

	public PersistentObjectPoolImpl()
	{
		super();
		persistentObjectsInMemory = new HashMap< PObjectOID, PersistentObject >();
	}
	
	@Override
	public synchronized void addPersistentObject( PersistentObject persistentObject )
	{
		persistentObjectsInMemory.put( persistentObject.getOID(), persistentObject );
	}

	@Override
	public void removePersistentObject( PObjectOID persistentObjectOID )
	{
		PersistentObject persistentObject = borrowPersistentObject( persistentObjectOID, true );
		
		try
		{
			synchronized ( this )
			{
				persistentObjectsInMemory.remove( persistentObject.getOID() );
				persistentObject.setReferencableInstance( false );
			}
		}
		finally
		{
			// Returning the object is critical to allow any waiting thread
			// to access it; of course, they will discover that it is invalid
			// and thus discard it, but without releasing the lock they would
			// continue waiting for ever.
			returnPersistentObject( persistentObject, true );
		}
	}
	
	@Override
	public PersistentObject getPersistentObject( PObjectOID persistentObjectOID )
	{
		PersistentObject persistentObject =  persistentObjectsInMemory.get( persistentObjectOID );
		
		if ( persistentObject == null )
			persistentObject = activate( persistentObjectOID );
		
		return persistentObject;
	}

	@Override
	public PersistentObject borrowPersistentObject( PObjectOID persistentObjectOID, boolean exclusiveAccess )
	{
		PersistentObject persistentObject = null;
		
		while ( (persistentObject = getPersistentObject( persistentObjectOID )) != null )
		{
			lockReferenceLock( persistentObject, exclusiveAccess );
			
			// While waiting on a lock (above) it is possible that the
			// object may have been invalidated due to, e.g., swapping
			// out or destruction. Thus we must check the isValidInstance
			// flag before returning. If that flag is false then the object
			// is either destroyed or swapped out. In the former case,
			// looping a second time will result in getpersistentObject()
			// returning null. In the later case, the object will be
			// swapped back in (since getPersistentObject() is overridden
			// in VolatileObjectPoolImpl) and we try again. The loop of
			// indefinite duration is necessary, since we can't assume
			// any specific number of tries until we get a definitive
			// result.
			if ( persistentObject.isReferencableInstance() )
				break;

			// Unlocking is important, as there could be another thread
			// waiting to access this object (even though it's invalid;
			// but another thread wouldn't know that yet).
			unlockReferenceLock( persistentObject, exclusiveAccess );
		}
		
		if ( persistentObject == null )
			throw new PersistentObjectNotFoundExeception( persistentObjectOID );
		
		return persistentObject;
	}

	@Override
	public void returnPersistentObject( PersistentObject persistentObject, boolean exclusiveAccess )
	{
		unlockReferenceLock( persistentObject, exclusiveAccess );
	}

	@Override
	public boolean persistentObjectExists( PObjectOID persistentObjectOID )
	{
		Boolean exists = false;
		
		if ( getPersistentObject( persistentObjectOID ) != null )
			exists = true;
		
		return exists;
	}
	
	@Override
	public void destroyPersistentObject( PObjectOID persistentObjectOID )
	{
		OmniObjectPool omniObjectPool = PEngine.getPEngine().getOmniObjectPool();
		
		// Find and destroy composite parts.
		if ( persistentObjectOID instanceof PProcessOID )
		{
			PProcessOID pProcessOID = (PProcessOID)persistentObjectOID;
			
			List< POwnableObjectOID > oids = PersistentOwnableObjectImpl.findOIDsByOwningProcess( pProcessOID, PersistentOwnableObjectImpl.class );
			
			for ( POwnableObjectOID oid : oids )
				omniObjectPool.removePersistentObject( oid );
		}
		
		
		// Finally, destroy the object itself.
		omniObjectPool.removePersistentObject( persistentObjectOID );
	}
	
	@Override
	public synchronized boolean visitPersistentObjects( PersistentObjectVisitor visitor, Class< ? > persistentObjectType )
	{
		boolean visitationInterrupted = visitPersistentObjectsTransient( visitor, persistentObjectType );
		
		if ( !visitationInterrupted )
			visitationInterrupted = visitPersistentObjectsPersistent( visitor, persistentObjectType );
		
		return visitationInterrupted;
	}

	private boolean visitPersistentObjectsPersistent( PersistentObjectVisitor visitor, Class< ? > persistentObjectType )
	{
		boolean visitationInterrupted = false;
		
		File persistentFileLocation = new File( pObjectsDir );
		String filterExpression = ( persistentObjectType == null ? "" : persistentObjectType.getSimpleName() );
		IOFileFilter pFileFilter = new WildcardFileFilter( filterExpression + "*" );
		Iterator< File > fileIter = FileUtils.iterateFiles( persistentFileLocation, pFileFilter, null );
	
		while ( fileIter.hasNext() )
		{
			File persistentObjectFile = fileIter.next();
			PObjectOID persistentObjectOID = getPersistentObjectOID( persistentObjectFile );
			PersistentObject persistentObject = load( persistentObjectOID );

			if ( persistentObjectType == null || persistentObjectType.isInstance( persistentObject ) )
				visitationInterrupted = visitor.visit( persistentObject );

			save( persistentObjectOID );
			
			if ( visitationInterrupted )
				break;
		}
		
		return visitationInterrupted;
	}

	private boolean visitPersistentObjectsTransient( PersistentObjectVisitor visitor, Class< ? > persistentObjectType )
	{
		boolean visitationInterrupted = false;
		
		for ( PersistentObject persistentObject : persistentObjectsInMemory.values() )
		{
			if ( persistentObjectType == null || persistentObjectType.isInstance( persistentObject ) )
				visitationInterrupted = visitor.visit( persistentObject );
			
			if ( visitationInterrupted )
				break;
		}
		
		return visitationInterrupted;
	}	
	private void lockReferenceLock( PersistentObject persistentObject, boolean exclusiveAccess )
	{
		if ( exclusiveAccess )
			persistentObject.getReferenceWriteLock().lock();
		else
			persistentObject.getReferenceReadLock().lock();
	}
	
	private void unlockReferenceLock( PersistentObject persistentObject, boolean exclusiveAccess )
	{
		if ( exclusiveAccess )
			persistentObject.getReferenceWriteLock().unlock();
		else
			persistentObject.getReferenceReadLock().unlock();
	}
	
	@Override
	public void start()
	{
		pObjectsDir = PEngine.getPEngine().getSaraswatiConfiguration().getPObjectsDir();
	}

	@Override
	public void stop()
	{
		while ( persistentObjectsInMemory.size() != 0 )
		{
			PObjectOID persistentObjectOID = persistentObjectsInMemory.keySet().iterator().next();
			
			if ( persistentObjectsInMemory.get( persistentObjectOID ).isPersistent() )
				passivate( persistentObjectOID );
			else
				removePersistentObject( persistentObjectOID );
		}
		
		pObjectsDir = null;
	}

	@Override
	public PersistentObject load( PObjectOID persistentObjectOID )
	{
		File persistentObjectFile = getPersistentObjectFile( persistentObjectOID );
		PersistentObject persistentObject = null;
		
		if ( persistentObjectFile.exists() )
		{
			try
			{
				FileInputStream fis = FileInputStreamWrapper.__new( persistentObjectFile );
				SaraswatiObjectInputStream sois = SaraswatiObjectInputStream.create( fis );
				persistentObject = (PersistentObject)sois.readObject();
				InputStreamWrapper.close( sois );
				InputStreamWrapper.close( fis );
				Files.delete( persistentObjectFile.toPath() );
				addPersistentObject( persistentObject );
			}
			catch ( Throwable t )
			{
				logger.log( Level.SEVERE, "Error activating persistent object. OID=" + persistentObjectOID, t );
			}
		}
		
		return persistentObject;
	}

	@Override
	public void save( PObjectOID persistentObjectOID )
	{
		PersistentObject persistentObject = (PersistentObject)borrowPersistentObject( persistentObjectOID, true );
		
		try
		{
			File persistentObjectFile = getPersistentObjectFile( persistentObject );
			FileOutputStream fos = FileOutputStreamWrapper.__new( persistentObjectFile );
			SaraswatiObjectOutputStream soos = SaraswatiObjectOutputStream.create( fos );
			soos.writeObject( persistentObject );
			soos.flush();
			fos.flush();
			OutputStreamWrapper.close( soos );
			OutputStreamWrapper.close( fos );
		}
		catch ( Throwable t )
		{
			logger.log( Level.SEVERE, "Error passivating persistent object. PID=" + persistentObjectOID, t );
		}
		finally
		{
			// Note that the object is removed even if an error ocurred.
			// This is necessary to avoid an infinite loop. In any case,
			// there really no way to recover. Also note that removePersistentObject()
			// invokes borrowPersistentObject()/returnPersistentObject() itself
			// causing a nested lock/unlock to be performed. This is valid,
			// since the reference lock is reentrant.
			removePersistentObject( persistentObjectOID );
			
			// Returning the object is critical to allow any waiting thread
			// to access it; of course, they will discover that it is invalid
			// and thus discard it, but without releasing the lock they would
			// continue waiting for ever.
			returnPersistentObject( persistentObject, true );
		}
	}
	
	@Override
	public PersistentObject activate( PObjectOID volatileObjectOID )
	{
		PersistentObject pObject = load( volatileObjectOID );
		
		if ( pObject != null )
			pObject.notifyActivate();
		
		return pObject;
	}

	@Override
	public void passivate( PObjectOID volatileObjectOID )
	{
		PersistentObject pObject = PersistentObjectImpl.getProxy( ProxyType.INTERNAL_OTHER, volatileObjectOID );
		pObject.notifyPassivate();
		save( volatileObjectOID );
	}

	private File getPersistentObjectFile( PersistentObject pObject )
	{
		String swapFileName = pObjectsDir + File.separator + pObject.getClass().getSimpleName() + "-" + pObject.getOID().toString();
		File swapFile = new File( swapFileName );
		
		return swapFile;
	}

	private File getPersistentObjectFile( PObjectOID persistentObjectOID )
	{
		File pObjectsBaseDir = new File( pObjectsDir );
		FileFilter pObjectFilter = new WildcardFileFilter( "*" + persistentObjectOID.toString() );
		File[] results = pObjectsBaseDir.listFiles( pObjectFilter );
		File swapFile = null;
		
		if ( results.length == 0 )
			swapFile = new File( "non-existant-file" );
		else if ( results.length == 1 )
			swapFile = results[ 0 ];
		else
			throw new IllegalStateException( "Unexpected length for results: " + results.length );
		
		return swapFile;
	}
	
	private PObjectOID getPersistentObjectOID( File persistentObjectFile )
	{
		String swapFileName = persistentObjectFile.getName();
		String pObjectClassName = getPObjectPackageName() + "." + swapFileName.substring( 0, swapFileName.indexOf( "-" ) );
		String oidAsString = swapFileName.substring( swapFileName.indexOf( "-" ) + 1 );
		
		Class< ? > pObjectClass = ClassWrapper.forName( pObjectClassName );
		UUID uuid = UUID.fromString( oidAsString );
		
		PObjectOID oid = PObjectOIDFactory.create( pObjectClass, uuid );
		
		return oid;
	}
	
	private String getPObjectPackageName()
	{
		String pObjectFQName = PersistentObjectImpl.class.getName();
		String pObjectPackageName = pObjectFQName.substring( 0, pObjectFQName.lastIndexOf( "." ) );

		return pObjectPackageName;
	}

	@Override
	public List< PObjectOID > findByName( final String name, Class< ? > persistentObjectType )
	{
		final List< PObjectOID > oids = new ArrayList< PObjectOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				if ( persistentObject.getName().matches( name ) )
					oids.add( persistentObject.getOID() );
				
				return false;
			}
		}, persistentObjectType );
		
		return oids;
	}

	@Override
	public List< PObjectOID > findByCreateDate( final Date createDateLowerBoundary, final Date createDateUpperBoundary, Class< ? > persistentObjectType )
	{
		final List< PObjectOID > oids = new ArrayList< PObjectOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				if ( !( persistentObject.getCreateDate().before( createDateLowerBoundary ) || persistentObject.getCreateDate().after( createDateUpperBoundary ) ) )
					oids.add( persistentObject.getOID() );
				
				return false;
			}
		}, persistentObjectType );
		
		return oids;
	}

	@Override
	public List< PObjectOID > findByLastActivityDate( final Date lastActivityDateLowerBoundary, final Date lastActivityDateUpperBoundary, Class< ? > persistentObjectType )
	{
		final List< PObjectOID > oids = new ArrayList< PObjectOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				if ( !( persistentObject.getLastActivityDate().before( lastActivityDateLowerBoundary ) || persistentObject.getLastActivityDate().after( lastActivityDateUpperBoundary ) ) )
					oids.add( persistentObject.getOID() );
				
				return false;
			}
		}, persistentObjectType );
		
		return oids;
	}
	
	@Override
	public List< POwnableObjectOID > findByOwningProcess( final PProcessOID owningProcessOID, Class< ? > persistentObjectType )
	{
		final List< POwnableObjectOID > oids = new ArrayList< POwnableObjectOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				PersistentOwnableObject pOwnableObject = (PersistentOwnableObject)persistentObject;
				PProcessOID otherOwningProcessOID = pOwnableObject.getOwningProcessOID();
				
				if ( otherOwningProcessOID != null && otherOwningProcessOID.equals( owningProcessOID ) )
					oids.add( pOwnableObject.getOID() );
				
				return false;
			}
		}, persistentObjectType );
		
		return oids;
	}

	@Override
	public PProcessOID findByOwnableObject( final POwnableObjectOID ownableObjectOID )
	{
		final Set< PProcessOID > oids = new HashSet< PProcessOID >();
		
		visitPersistentObjects( new PersistentObjectVisitor() {
			@Override
			public boolean visit( PersistentObject persistentObject )
			{
				PersistentProcess pProcess = (PersistentProcess)persistentObject;
				Set< POwnableObjectOID > ownableObjectOIDs = pProcess.getOwnableObjectOIDs();
				
				if ( ownableObjectOIDs != null && ownableObjectOIDs.contains( ownableObjectOID ) )
					oids.add( pProcess.getOID() );
				
				return true;
			}
		}, PersistentProcessImpl.class );
		
		return oids.iterator().next();
	}
}
