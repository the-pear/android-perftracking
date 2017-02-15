package jp.co.rakuten.sdtd.perf.runtime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

public class EventWriter extends BufferedWriter {
	
	private final HttpsURLConnection _conn;

	public EventWriter(HttpsURLConnection conn, OutputStream outputStream) {
		super(new OutputStreamWriter(outputStream));		
		_conn = conn;
	}
	
	@Override
	public void close() throws IOException {
		try {
			super.close();
			int result = _conn.getResponseCode();
			if (result != 201) {
				throw new IOException("Failed to send event with status " + result);
			}
		}
		finally {
			_conn.disconnect();
		}
	}
}
