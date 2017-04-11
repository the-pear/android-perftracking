package com.rakuten.tech.mobile.perf.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public class EventWriter {
    private final String TAG = "Performance Tracking";
    private final Config _config;
    private final EnvironmentInfo _envInfo;
    private HttpsURLConnection _conn;
    private BufferedWriter _writer;
    private int _measurements;

    public EventWriter(Config config, EnvironmentInfo envInfo) {
        _config = config;
        _envInfo = envInfo;
    }

    public void begin() {
        try {
            _conn = (HttpsURLConnection) ((new URL(_config.eventHubUrl).openConnection()));
            _conn.setRequestMethod("POST");
            for (Map.Entry<String, String> entry : _config.header.entrySet())
                _conn.setRequestProperty(entry.getKey(), entry.getValue());
            _conn.setUseCaches(false);
            _conn.setDoInput(false);
            _conn.setDoOutput(true);
            //conn.setConnectTimeout(10000);
            _conn.connect();

            _writer = new BufferedWriter(new OutputStreamWriter(_conn.getOutputStream()));
            _writer.append("{\"app\":\"").append(escapeValue(_config.app))
                    .append("\",\"version\":\"").append(escapeValue(_config.version));

            if (_envInfo.device != null) {
                _writer.append("\",\"device\":\"").append(escapeValue(_envInfo.device));
            }

            if (_envInfo.country != null) {
                _writer.append("\",\"country\":\"").append(escapeValue(_envInfo.country));
            }

            if (_envInfo.network != null) {
                _writer.append("\",\"network\":\"").append(escapeValue(_envInfo.network));
            }

            _writer.append("\",\"measurements\":[");
            _measurements = 0;

        } catch (Exception e) {
            if (_config.debug) {
                Log.d(TAG, e.toString());
            }
            disconnect();
        }
    }

    public void write(Metric metric) {
        if (_writer != null) {
            try {
                if (_measurements > 0) {
                    _writer.append(',');
                }
                _writer
                        .append("{\"metric\":\"").append(escapeValue(metric.id))
                        .append("\",\"urls\":").append(escapeValue(Integer.toString(metric.urls)))
                        .append(",\"time\":").append(escapeValue(Integer.toString((int) ((metric.endTime - metric.startTime) / 1000000))))
                        .append('}');
                _measurements++;
            } catch (Exception e) {
                if (_config.debug) {
                    Log.d(TAG, e.toString());
                }
                disconnect();
            }
        }
    }

    public void write(Measurement m, String metricId) {
        if (_writer != null) {
            try {
                if (_measurements > 0) {
                    _writer.append(',');
                }

                switch (m.type) {
                    case Measurement.METHOD:
                        _writer.append("{\"method\":\"").append(escapeValue((String) m.a)).append('#').append(escapeValue((String) m.b)).append('"');
                        break;

                    case Measurement.URL:
                        _writer.append("{\"url\":\"");

                        if (m.a instanceof URL) {
                            URL url = (URL) m.a;
                            _writer.append(escapeValue(url.getProtocol())).append("://").append(escapeValue(url.getAuthority())).append(escapeValue(url.getPath()));
                        } else {
                            String url = (String) m.a;
                            int q = url.indexOf('?');
                            if (q > 0) {
                                url = url.substring(0, q);
                            }
                            _writer.append(escapeValue(url));
                        }

                        _writer.append('"');

                        if (m.b != null) {
                            _writer.append(",\"verb\":\"").append(escapeValue((String) m.b)).append('"');
                        }
                        break;

                    case Measurement.CUSTOM:
                        _writer.append("{\"custom\":\"").append(escapeValue((String) m.a)).append('"');
                        break;

                    default:
                        return;
                }

                if (metricId != null) {
                    _writer.append(",\"metric\":\"").append(escapeValue(metricId)).append('"');
                }
                _writer.append(",\"time\":").append(escapeValue(Integer.toString((int) ((m.endTime - m.startTime) / 1000000)))).append('}');
                _measurements++;
            } catch (Exception e) {
                if (_config.debug) {
                    Log.d(TAG, e.toString());
                }
                disconnect();
            }
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
        } catch (Exception e) {
            if (_config.debug) {
                Log.d(TAG, e.toString());
            }
        } finally {
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

    private String escapeValue(String s) {
        return s.replace("\"", "\\\"");
    }
}
