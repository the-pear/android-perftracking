package jp.co.rakuten.sdtd.perf.runtime;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import android.util.Log;

public class Sender implements MeasurementProcessor {
	
	private final Config _config;
	private final EnvironmentInfo _envInfo;
	private final EventHub _eventHub;
	private final MeasurementBuffer _measurementBuffer;
	private final Snapshotter _snapshotter;
	private final Snapshot _snapshot = new Snapshot();
	private Writer _writer;
	private int _written;
	private boolean _debug;
	
	public Sender(Config config, EnvironmentInfo envInfo, EventHub eventHub, MeasurementBuffer measurementBuffer, Snapshotter snapshotter, boolean debug) {
		_config = config;
		_envInfo = envInfo;
		_eventHub = eventHub;
		_measurementBuffer = measurementBuffer;
		_snapshotter = snapshotter;
		_debug = debug;
	}
	
	public void send() throws IOException {
		if (_debug) {
			Log.d("PERF", "Sending...");
		}
		
		_writer = _eventHub.open();
		
		try {
			_writer.append("{\"app\":\"").append(_config.app)
			.append("\",\"version\":\"").append(_config.version);
			
			if (_envInfo.device != null)
			{
				_writer.append("\",\"device\":\"").append(_envInfo.device);
			}
			
			if (_envInfo.country != null)
			{
				_writer.append("\",\"country\":\"").append(_envInfo.country);
			}
			
			if (_envInfo.network != null)
			{
				_writer.append("\",\"network\":\"").append(_envInfo.network);
			}

			_writer.append("\",\"measurements\":[");
			
			_written = 0;
			_measurementBuffer.flush(this);
			
			_writer.append("]}");
		}
		finally {
			_writer.close();
		}
	}
	
	public void process(int measurementId) throws IOException {
		if (!_snapshotter.take(measurementId, _snapshot)) {
			return;
		}
		
		if (_snapshot.endTime == 0) {
			return;
		}

		switch (_snapshot.type) {
		case Measurement.METRIC:
			if (_written > 0) {
				_writer.append(',');
			}
			
			_writer
				.append("{\"metric\":\"").append(_snapshot.metricId)
				.append("\",\"urls\":").append(Integer.toString(_snapshot.metricUrls));
			break;
			
		case Measurement.METHOD:
			if (_written > 0) {
				_writer.append(',');
			}
			
			_writer.append("{\"method\":\"").append((String)_snapshot.a).append('.').append((String)_snapshot.b).append('"');
			
			if (_snapshot.metricId != null) {
				_writer.append(",\"metric\":\"").append(_snapshot.metricId).append('"');
			}
			break;

		case Measurement.URL:
			if (_written > 0) {
				_writer.append(',');
			}
			
			URL url = (URL)_snapshot.a;
			_writer.append("{\"url\":\"").append(url.getProtocol()).append("://").append(url.getAuthority()).append(url.getPath()).append('"');
			
			if (_snapshot.b != null) {
				_writer.append(",\"verb\":\"").append((String)_snapshot.b).append('"');
			}
			
			if (_snapshot.metricId != null) {
				_writer.append(",\"metric\":\"").append(_snapshot.metricId).append('"');
			}
			break;

		case Measurement.CUSTOM:
			if (_written > 0) {
				_writer.append(',');
			}
			
			_writer.append("{\"custom\":\"").append((String)_snapshot.a).append('"');
			
			if (_snapshot.metricId != null) {
				_writer.append(",\"metric\":\"").append(_snapshot.metricId).append('"');
			}
			break;
			
		default:
			return;
		}
		
		int time = (int)((_snapshot.endTime - _snapshot.startTime) / 1000000);
		if (time == 0) {
			time = 1;	// round to 1ms
		}
		
		_writer.append(",\"time\":").append(Integer.toString(time)).append('}');
		_written++;
	}
}
