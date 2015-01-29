package ch.shaktipat.saraswati.internal.web.model.pprocess;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.shaktipat.saraswati.internal.web.model.PObjectModel;
import ch.shaktipat.saraswati.pobject.oid.PObjectOID;
import ch.shaktipat.saraswati.volatility.Volatility;

public class PProcessDetailModel extends PProcessListRowModel
{
	private Date lastActivityDate;
	private Volatility initialVolatility;
	private boolean destroyOnCompletion;
	private boolean autoCheckpointing;
	private String[] allStates;
	private ProcessResult processResult;
	private String systemLog;
	private String applicationLog;
	private List< PObjectModel > pEventQueues;
	private List< PObjectModel > pEventTopics;
	private StackTraceElement[] stackTrace;
	public Class< ? > runnableType;
	public Class< ? > callableType;
	
	public PProcessDetailModel( boolean commandSuccess, PObjectOID oid, String name, Date createDate, Principal owner, String state, String volatility,
			 Date lastActivityDate, Volatility initialVolatility, boolean destroyOnCompletion, boolean autoCheckpointing, String[] allStates,
			 ProcessResult processResult, String systemLog, String applicationLog, List< PObjectModel > pEventQueues, List< PObjectModel > pEventTopics,
			 StackTraceElement[] stackTrace, Class< ? > runnableType, Class< ? > callableType )
	{
		super( commandSuccess, oid, name, createDate, owner, state, volatility );
		this.lastActivityDate = lastActivityDate;
		this.initialVolatility = initialVolatility;
		this.destroyOnCompletion = destroyOnCompletion;
		this.autoCheckpointing = autoCheckpointing;
		this.allStates = allStates;
		this.processResult = processResult;
		this.systemLog = systemLog;
		this.applicationLog = applicationLog;
		this.pEventQueues = pEventQueues;
		this.pEventTopics = pEventTopics;
		this.stackTrace = stackTrace;
		this.runnableType = runnableType;
		this.callableType = callableType;
	}
	
	public PProcessDetailModel( boolean commandSuccess )
	{
		super( commandSuccess, null, null, null, null, null, null );
	}

	public Date getLastActivityDate()
	{
		return lastActivityDate;
	}

	public void setLastActivityDate( Date lastActivityDate )
	{
		this.lastActivityDate = lastActivityDate;
	}

	public Volatility getInitialVolatility()
	{
		return initialVolatility;
	}

	public void setInitialVolatility( Volatility initialVolatility )
	{
		this.initialVolatility = initialVolatility;
	}

	public boolean getDestroyOnCompletion()
	{
		return destroyOnCompletion;
	}

	public void setDestroyOnCompletion( boolean destroyOnCompletion )
	{
		this.destroyOnCompletion = destroyOnCompletion;
	}

	public boolean getAutoCheckpointing()
	{
		return autoCheckpointing;
	}

	public void setAutoCheckpointing( boolean autoCheckpointing )
	{
		this.autoCheckpointing = autoCheckpointing;
	}

	public String[] getAllStates()
	{
		return allStates;
	}

	public void setAllStates( String[] allStates )
	{
		this.allStates = allStates;
	}

	public ProcessResult getProcessResult()
	{
		return processResult;
	}

	public void setProcessResult( ProcessResult processResult )
	{
		this.processResult = processResult;
	}

	public String getSystemLog()
	{
		return systemLog;
	}

	public void setSystemLog( String systemLog )
	{
		this.systemLog = systemLog;
	}

	public String getApplicationLog()
	{
		return applicationLog;
	}

	public void setApplicationLog( String applicationLog )
	{
		this.applicationLog = applicationLog;
	}

	public List< PObjectModel > getpEventQueues()
	{
		return pEventQueues;
	}

	public void setpEventQueues( List< PObjectModel > pEventQueues )
	{
		this.pEventQueues = pEventQueues;
	}

	public List< PObjectModel > getpEventTopics()
	{
		return pEventTopics;
	}

	public void setpEventTopics( List< PObjectModel > pEventTopics )
	{
		this.pEventTopics = pEventTopics;
	}

	public StackTraceElement[] getStackTrace()
	{
		return stackTrace;
	}

	public void setStackTrace( StackTraceElement[] stackTrace )
	{
		this.stackTrace = stackTrace;
	}

	public Class< ? > getRunnableType()
	{
		return runnableType;
	}

	public void setRunnableType( Class< ? > runnableType )
	{
		this.runnableType = runnableType;
	}

	public Class< ? > getCallableType()
	{
		return callableType;
	}

	public void setCallableType( Class< ? > callableType )
	{
		this.callableType = callableType;
	}

	@Override
	public String toString()
	{
		return "PProcessDetailModel [lastActivityDate=" + lastActivityDate + ", initialVolatility=" + initialVolatility + ", destroyOnCompletion=" + destroyOnCompletion + ", autoCheckpointing="
				+ autoCheckpointing + ", allStates=" + Arrays.toString( allStates ) + ", processResult=" + processResult + ", systemLog=" + systemLog + ", applicationLog=" + applicationLog
				+ ", pEventQueues=" + pEventQueues + ", pEventTopics=" + pEventTopics + ", stackTrace=" + Arrays.toString( stackTrace ) + ", runnableType=" + runnableType + ", callableType="
				+ callableType + "]";
	}
}
