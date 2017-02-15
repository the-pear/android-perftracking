package jp.co.rakuten.sdtd.perf.runtime;

import java.net.URL;
import android.util.Log;

public class TrackerImpl {
	private final long METRIC_LINGER_TIME = 200000000L; // 100 ms
	
	private final MeasurementBuffer _measurementBuffer;
	private final SendTrigger _sendTrigger;
	private final boolean _debug;
	private Metric _currentMetric;
	
    public TrackerImpl(MeasurementBuffer measurementBuffer, SendTrigger sendTrigger, boolean debug) {
    	_measurementBuffer = measurementBuffer;
    	_sendTrigger = sendTrigger;
    	_debug = debug;
    }
    
	public void startMetric(String metricId) {
		long now = System.nanoTime();
		
		Metric metric = _currentMetric;
		if (metric != null)
		{
			endMetric(metric);
		}
		
		metric = new Metric();
		metric.id = metricId;
		metric.startTime = now;
		
		_currentMetric = metric;
		
		if (_debug) {
			Log.d("PERF", "startMetric: " + metricId);
		}
	}

	private void endMetric(Metric metric) {
		if (_debug) {
			Log.d("PERF", "endMetric: " + metric.id);
		}
		
		if (_currentMetric == metric)
		{
			_currentMetric = null;
		}
		
		if (metric.measurementsInProgress.get() > 0) {
			return;
		}
		
		if (metric.unconfirmedEndTime == 0) {
			return;
		}
		
		Measurement m = _measurementBuffer.next();
		m.type = Measurement.METRIC;
		m.metric = metric;
		m.startTime = metric.startTime;
		m.endTime = metric.unconfirmedEndTime;
		
		_sendTrigger.measurementEnded();
	}
	
	public int startMethod(Object object, String method) {
		if ((object == null) || (method == null)) {
			return 0;
		}
		
		return startMeasurement(Measurement.METHOD, object.getClass().getName(), method);
	}
	
	public void endMethod(int trackingId) {
		endMeasurement(trackingId);
	}

	public int startUrl(URL url, String verb) {
		if (url == null) {
			return 0;
		}
			
		return startMeasurement(Measurement.URL, url, verb);
	}
	
	public void endUrl(int trackingId) {
		endMeasurement(trackingId);
	}

	public int startCustom(String measurementId) {
		if (measurementId == null) {
			return 0;
		}
			
		return startMeasurement(Measurement.CUSTOM, measurementId, null);
	}
	
	public void endCustom(int trackingId) {
		endMeasurement(trackingId);
	}
	
	private int startMeasurement(byte type, Object a, Object b) {
		long now = System.nanoTime();
		
		Metric metric = _currentMetric;
		if (metric != null)
		{
			if (metric.measurementsInProgress.get() == 0)
			{
				long time = metric.unconfirmedEndTime != 0 ? metric.unconfirmedEndTime : metric.startTime;
				if (now - time > METRIC_LINGER_TIME)
				{
					endMetric(metric);
					metric = null;
				}
			}
		}
		
		if (metric != null)
		{
			metric.measurementsInProgress.incrementAndGet();
			
			if (type == Measurement.URL)
			{
				metric.urls.incrementAndGet();
			}
		}
		
		Measurement m = _measurementBuffer.next();
		m.type = type;
		m.metric = metric;
		m.a = a;
		m.b = b;
		m.startTime = now;

		if (_debug) {
			log("start", m);
		}
		
		return m.trackingId;
	}
	
	private void endMeasurement(int trackingId) {
		if (trackingId != 0) {
			long now = System.nanoTime();
			
			Measurement m = _measurementBuffer.get(trackingId);
			if (m != null)
			{
				m.endTime = now;
				
				Metric metric = m.metric;
				if ((metric != null) && (metric == _currentMetric))
				{
					if (metric.measurementsInProgress.decrementAndGet() == 0)
					{
						metric.unconfirmedEndTime = now;
					}
				}
			
				_sendTrigger.measurementEnded();
				
				if (_debug) {
					log("end", m);
				}
			}
		}
	}
	
	private void log(String action, Measurement m) {
		StringBuilder s = new StringBuilder();
		
		s.append(action).append(": ");
		s.append("trackingId=").append(m.trackingId);
		s.append(",type=").append(m.type);
		
		if (m.a != null) {
			s.append(",a=").append(m.a);
		}
		
		if (m.b != null) {
			s.append(",b=").append(m.b);
		}
		
		s.append(",startTime=").append(m.startTime);
		s.append(",endTime=").append(m.endTime);
		
		Metric metric = m.metric;
		if (metric != null) {
			s.append(",metric.id=").append(metric.id);
			s.append(",metric.measurementsInProgress=").append(metric.measurementsInProgress.get());
			s.append(",metric.unconfirmedEndTime=").append(metric.unconfirmedEndTime);
			s.append(",metric.urls=").append(metric.urls);
		}
		
		Log.d("PERF", s.toString());
	}
}
