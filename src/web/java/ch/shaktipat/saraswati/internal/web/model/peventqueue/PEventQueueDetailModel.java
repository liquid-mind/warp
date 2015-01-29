package ch.shaktipat.saraswati.internal.web.model.peventqueue;

import java.util.Date;
import java.util.List;

import ch.shaktipat.saraswati.internal.web.model.POwnableObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.pobject.oid.PProcessOID;

public class PEventQueueDetailModel extends POwnableObjectModel
{
	private PProcessOID listeningProcessOID;
	private String listeningProcessName;
	private String listeningFilter;
	private Date listeningTimeout;
	private List< Class< ? > > queuedEventTypes;
	
	public PEventQueueDetailModel(
			boolean commandSuccess,
			PObjectOID oid,
			String name,
			Date createDate,
			PProcessOID owningProcessOID,
			String owningProcessName,
			PProcessOID listeningProcessOID,
			String listeningProcessName,
			String listeningFilter,
			Date listeningTimeout,
			List< Class< ? >> queuedEventTypes )
	{
		super( commandSuccess, oid, name, createDate, owningProcessOID, owningProcessName );
		this.listeningProcessOID = listeningProcessOID;
		this.listeningProcessName = listeningProcessName;
		this.listeningFilter = listeningFilter;
		this.listeningTimeout = listeningTimeout;
		this.queuedEventTypes = queuedEventTypes;
	}

	public PEventQueueDetailModel( boolean commandSuccess )
	{
		super( commandSuccess, null, null, null, null, null );
	}

	public PProcessOID getListeningProcessOID()
	{
		return listeningProcessOID;
	}

	public void setListeningProcessOID( PProcessOID listeningProcessOID )
	{
		this.listeningProcessOID = listeningProcessOID;
	}

	public String getListeningProcessName()
	{
		return listeningProcessName;
	}

	public void setListeningProcessName( String listeningProcessName )
	{
		this.listeningProcessName = listeningProcessName;
	}

	public String getListeningFilter()
	{
		return listeningFilter;
	}

	public void setListeningFilter( String listeningFilter )
	{
		this.listeningFilter = listeningFilter;
	}

	public Date getListeningTimeout()
	{
		return listeningTimeout;
	}

	public void setListeningTimeout( Date listeningTimeout )
	{
		this.listeningTimeout = listeningTimeout;
	}

	public List< Class< ? >> getQueuedEventTypes()
	{
		return queuedEventTypes;
	}

	public void setQueuedEventTypes( List< Class< ? >> queuedEventTypes )
	{
		this.queuedEventTypes = queuedEventTypes;
	}
}
