package jp.co.rakuten.sdtd.perf.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public class EventWriter {

	private final Config _config;
	private final EnvironmentInfo _envInfo;
	private final String _brokerProperties;
	private HttpsURLConnection _conn;
	private BufferedWriter _writer;
	private int _measurementCount;

	public EventWriter(Config config, EnvironmentInfo envInfo) {
		_config = config;
		_envInfo = envInfo;
		_brokerProperties = "{\"PartitionKey\": \"" + config.app + "/" + config.version + "\"}";
	}

	public void begin() {
		try {
			_conn = (HttpsURLConnection) ((new URL (_config.eventHubUrl).openConnection()));
			_conn.setRequestMethod("POST");
			_conn.setRequestProperty("Authorization", _config.eventHubAuthorization);
			_conn.setRequestProperty("Content-Type", "application/atom+xml;type=entry;charset=utf-8");
			_conn.setRequestProperty("BrokerProperties", _brokerProperties);
			_conn.setUseCaches(false);
			_conn.setDoInput(false);
			_conn.setDoOutput(true);
			//conn.setConnectTimeout(10000);
			_conn.connect();

			_writer = new BufferedWriter(new OutputStreamWriter(_conn.getOutputStream()));
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

			_measurementCount = 0;
		}
		catch (Exception e) {
			if (_config.debug) {
				Log.d("PERF", e.toString());
			}
			disconnect();
		}
	}

	public void write(Measurement m, Metric metric) {
		try {
			if (_writer != null) {
				if (_measurementCount > 0) {
					_writer.append(',');
				}

				switch (m.type) {
					case Measurement.METRIC:
						metric = (Metric)m.a;
						_writer
								.append("{\"metric\":\"").append(metric.id)
								.append("\",\"urls\":").append(Integer.toString(metric.urls));
						break;

					case Measurement.METHOD:
						_writer.append("{\"method\":\"").append((String)m.a).append('.').append((String)m.b).append('"');
						break;

					case Measurement.URL:
						URL url = (URL)m.a;
						_writer.append("{\"url\":\"").append(url.getProtocol()).append("://").append(url.getAuthority()).append(url.getPath()).append('"');
						if (m.b != null) {
							_writer.append(",\"verb\":\"").append((String)m.b).append('"');
						}
						break;

					case Measurement.CUSTOM:
						_writer.append("{\"custom\":\"").append((String)m.a).append('"');
						break;

					default:
						return;
				}

				if (m.type != Measurement.METRIC) {
					if (metric != null) {
						_writer.append(",\"metric\":\"").append(metric.id).append('"');
					}
				}

				int time = (int)((m.endTime - m.startTime) / 1000000);
				if (time == 0) {
					time = 1;	// round to 1ms
				}

				_writer.append(",\"time\":").append(Integer.toString(time)).append('}');

				_measurementCount++;
			}
		}
		catch (Exception e) {
			if (_config.debug) {
				Log.d("PERF", e.toString());
			}
			disconnect();
		}
	}

	public void end() {
		try {
			if (_writer != null) {
				_writer.append("]}");
				_writer.close();

				int result = _conn.getResponseCode();
				if (result != 201) {
					throw new IOException("Failed to send event with status " + result);
				}
			}
		}
		catch (Exception e) {
			if (_config.debug) {
				Log.d("PERF", e.toString());
			}
		}
		finally {
			disconnect();
		}
	}

	private void disconnect() {
		if (_conn != null) {
			_conn.disconnect();
		}
		_conn = null;
		_writer = null;
	}
}
