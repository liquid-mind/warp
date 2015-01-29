package ch.shaktipat.saraswati.internal.common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class MeasurementHelper
{
	private transient ThreadMXBean threadMXBean;
	private Map< String, Long > beginMarkerCpuMap = new HashMap< String, Long >();
	private Map< String, Long > beginMarkerUserMap = new HashMap< String, Long >();
	private Map< String, Long > beginMarkerElapsedMap = new HashMap< String, Long >();
	private Map< String, Long > endMarkerCpuMap = new HashMap< String, Long >();
	private Map< String, Long > endMarkerUserMap = new HashMap< String, Long >();
	private Map< String, Long > endMarkerElapsedMap = new HashMap< String, Long >();

	public MeasurementHelper()
	{
		threadMXBean = ManagementFactory.getThreadMXBean();
	}

	public void markBegin( String measurementId )
	{
		assert ( measurementId != null );
		assert ( !measurementId.isEmpty() );

		beginMarkerCpuMap.put( measurementId, threadMXBean.getCurrentThreadCpuTime() );
		beginMarkerUserMap.put( measurementId, threadMXBean.getCurrentThreadUserTime() );
		beginMarkerElapsedMap.put( measurementId, System.nanoTime() );
	}

	public void markEnd( String measurementId )
	{
		assert ( measurementId != null );
		assert ( !measurementId.isEmpty() );

		endMarkerCpuMap.put( measurementId, threadMXBean.getCurrentThreadCpuTime() );
		endMarkerUserMap.put( measurementId, threadMXBean.getCurrentThreadUserTime() );
		endMarkerElapsedMap.put( measurementId, System.nanoTime() );
	}

	public void outputRatio( Logger logger, String measurementId1, String measurementId2 )
	{
		long cpuTime1 = endMarkerCpuMap.get( measurementId1 ) - beginMarkerCpuMap.get( measurementId1 );
		long cpuTime2 = endMarkerCpuMap.get( measurementId2 ) - beginMarkerCpuMap.get( measurementId2 );
		long userTime1 = endMarkerUserMap.get( measurementId1 ) - beginMarkerUserMap.get( measurementId1 );
		long userTime2 = endMarkerUserMap.get( measurementId2 ) - beginMarkerUserMap.get( measurementId2 );
		long elapsedTime1 = endMarkerElapsedMap.get( measurementId1 ) - beginMarkerElapsedMap.get( measurementId1 );
		long elapsedTime2 = endMarkerElapsedMap.get( measurementId2 ) - beginMarkerElapsedMap.get( measurementId2 );

		logger.fine( "CPU time ratio ( measurementId1 / measurementId2 ):" + ( cpuTime1 / cpuTime2 ) );
		logger.fine( "User time ratio ( measurementId1 / measurementId2 ):" + ( userTime1 / userTime2 ) );
		logger.fine( "Elapsed time ratio ( measurementId1 / measurementId2 ):" + ( elapsedTime1 / elapsedTime2 ) );
	}

	public void outputAllMarkers( Logger logger )
	{
		assert ( logger != null );

		Set< String > markerSet = beginMarkerCpuMap.keySet();

		for ( String marker : markerSet )
		{
			long cpuTimeNs = endMarkerCpuMap.get( marker ) - beginMarkerCpuMap.get( marker );
			long cpuTimeMs = cpuTimeNs / 1000000;
			logger.info( "CPU time for marker " + marker + ": " + cpuTimeNs + "ns = " + cpuTimeMs + "ms" );

			long userTimeNs = endMarkerUserMap.get( marker ) - beginMarkerUserMap.get( marker );
			long userTimeMs = userTimeNs / 1000000;
			logger.info( "User time for marker " + marker + ": " + userTimeNs + "ns = " + userTimeMs + "ms" );

			long elapsedTimeNs = endMarkerElapsedMap.get( marker ) - beginMarkerElapsedMap.get( marker );
			long elapsedTimeMs = elapsedTimeNs / 1000000;
			logger.info( "Elapsed time for marker " + marker + ": " + elapsedTimeNs + "ns = " + elapsedTimeMs + "ms" );
		}
	}

	public void clearMarkers()
	{
		beginMarkerCpuMap.clear();
		beginMarkerUserMap.clear();
		beginMarkerElapsedMap.clear();
		endMarkerCpuMap.clear();
		endMarkerUserMap.clear();
		endMarkerElapsedMap.clear();
	}
}
