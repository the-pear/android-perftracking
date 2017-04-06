package com.rakuten.tech.mobile.perf.core;

public class Sender {
	public static final long MIN_TIME = 5000000L; // 5 ms

	private final MeasurementBuffer _buffer;
	private final Current _current;
	private final EventWriter _writer;
	private final Debug _debug;
	private Metric _metric;
	private int _sent;

	public Sender(MeasurementBuffer buffer, Current current, EventWriter writer, Debug debug) {
		_buffer = buffer;
		_current = current;
		_writer = writer;
		_debug = debug;
	}

	public int send(int startIndex, int endIndex) {
		_sent = 0;
		long now = System.nanoTime();

		try {
			for (int i = startIndex; i != endIndex; i = (i + 1) % MeasurementBuffer.SIZE) {
				Measurement m = _buffer.at[i];

				if (m.type == Measurement.METRIC) {

					if (_metric != null)
					{
						send(_metric);
						_metric = null;
					}

					Metric metric = (Metric)m.a;

					if (metric == _current.metric.get()) {
						if (now - m.startTime < Metric.MAX_TIME) {
							return i;
						}

						_current.metric.compareAndSet(metric, null);
					}

					_metric = metric;
					m.clear();
				}
				else {
					if (m.endTime == 0) {
						if (now - m.startTime < Measurement.MAX_TIME) {
							return i;
						}

						m.clear();
						continue;
					}

					if ((_metric != null) && (m.startTime > _metric.endTime)) {
						send(_metric);
						_metric = null;
					}

					if ((_metric != null) && (m.type == Measurement.URL)) {
						_metric.urls++;
					}

					send(m, _metric != null ? _metric.id : null);
					m.clear();
				}
			}

			return endIndex;
		}
		finally {
			if (_sent > 0) {
				_writer.end();
			}
		}
	}

	private void send(Metric metric) {
		if (metric.endTime - metric.startTime < MIN_TIME) {
			return;
		}

		if (_debug != null) {
			_debug.log("SEND_METRIC", metric);
		}

		if (_sent == 0) {
			_writer.begin();
		}

		_writer.write(metric);
		_sent++;
	}

	private void send(Measurement m, String metricId) {
		if (m.endTime - m.startTime < MIN_TIME) {
			return;
		}

		if (_debug != null) {
			_debug.log("SEND", m, metricId);
		}

		if (_sent == 0) {
			_writer.begin();
		}

		_writer.write(m, metricId);

		_sent++;
	}
}
