package jp.co.rakuten.sdtd.perf.runtime;

public class Snapshotter {
	private final MeasurementBuffer _measurementBuffer;
	
	public Snapshotter(MeasurementBuffer measurementBuffer) {
		_measurementBuffer = measurementBuffer;
	}
	
	public boolean take(int trackingId, Snapshot snapshot)
	{
		Measurement measurement = _measurementBuffer.get(trackingId);
		if (measurement == null)
		{
			return false;
		}
		
		Metric metric = measurement.metric;
		
		snapshot.type = measurement.type;
		snapshot.a = measurement.a;
		snapshot.b = measurement.b;
		snapshot.startTime = measurement.startTime;
		snapshot.endTime = measurement.endTime;

		if (metric != null)
		{
			snapshot.metricId = metric.id;
			snapshot.metricUrls = metric.urls.get();
		}
		
		if (measurement.trackingId != trackingId)
		{
			return false;
		}
		
		return true;
	}
}
