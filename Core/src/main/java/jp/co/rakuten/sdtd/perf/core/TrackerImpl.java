package jp.co.rakuten.sdtd.perf.core;

import java.net.URL;

public class TrackerImpl {
	private final MeasurementBuffer _measurementBuffer;
	private final Debug _debug;

	public TrackerImpl(MeasurementBuffer measurementBuffer, Debug debug) {
		_measurementBuffer = measurementBuffer;
		_debug = debug;
	}

	public void startMetric(String metricId) {
		Metric metric = new Metric();
		metric.id = metricId;
		startMeasurement(Measurement.METRIC, metric, null);
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
		Measurement m = _measurementBuffer.next();
		if (m == null) {
			return 0;
		}

		m.type = type;
		m.a = a;
		m.b = b;
		m.startTime = System.nanoTime();

		if (_debug != null) {
			log("start", m);
		}

		return m.trackingId;
	}

	private void endMeasurement(int trackingId) {
		if (trackingId != 0) {
			Measurement m = _measurementBuffer.getByTrackingId(trackingId);
			if (m != null)
			{
				m.endTime = System.nanoTime();

				if (_debug != null) {
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

		if (m.type == Measurement.METRIC) {
			s.append(",metric=").append(((Metric)m.a).id);
		}
		else {
			if (m.a != null) {
				s.append(",a=").append(m.a);
			}

			if (m.b != null) {
				s.append(",b=").append(m.b);
			}
		}

		s.append(",startTime=").append(m.startTime);
		s.append(",endTime=").append(m.endTime);

		if (m.endTime > m.startTime) {
			s.append(",time=").append((m.endTime - m.startTime) / 1000000);
		}

		_debug.log(s.toString());
	}
}
