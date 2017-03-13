package jp.co.rakuten.sdtd.perf.core;

import android.util.Log;

public class Sender {

	private final MeasurementBuffer _buffer;
	private final MetricCalculator _metricCalculator;
	private final EventWriter _writer;
	private final Debug _debug;

	public Sender(MeasurementBuffer buffer, MetricCalculator metricCalculator, EventWriter writer, Debug debug) {
		_buffer = buffer;
		_metricCalculator = metricCalculator;
		_writer = writer;
		_debug = debug;
	}

	public int send(int startIndex, int endIndex) {
		long now = System.nanoTime();

		Metric metric = null;
		long metricStartTime = 0;
		long metricEndTime = 0;

		_writer.begin();

		try {
			for (int i = startIndex; i != endIndex; i = (i + 1) % MeasurementBuffer.SIZE) {
				Measurement m = _buffer.at[i];

				if (m.type == Measurement.METRIC) {
					if (!_metricCalculator.calculate(m, i + 1, endIndex)) {
						return i;
					}

					metric = (Metric)m.a;
					metricStartTime = m.startTime;
					metricEndTime = m.endTime;

					if (_debug != null) {
						_debug.log("Metric " + metric.id + ": startTime=" + m.startTime + ", endTime=" + m.endTime + ", time=" + ((m.endTime - m.startTime) / 1000000) + ", urls=" + metric.urls);
					}

					send(m, metric);
				}
				else {
					long startTime = m.startTime;
					long endTime = m.endTime;

					if (endTime == 0) {
						if (now - startTime < Measurement.TIMEOUT) {
							return i;
						}

						m.clear();
						continue;
					}

					send(m, (metric != null) && (startTime >= metricStartTime) && (endTime <= metricEndTime) ? metric : null);
				}
			}

			return endIndex;
		}
		finally {
			_writer.end();
		}
	}

	private void send(Measurement m, Metric metric) {
		if (_debug != null) {
			_debug.log("Sending " + m.trackingId);
		}

		_writer.write(m, metric);
		m.clear();
	}
}
