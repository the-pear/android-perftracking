package jp.co.rakuten.sdtd.perf.core;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class EventHub {
	
	private final Config _config;
	private final String _brokerProperties;
	
	public EventHub(Config config) {
		_config = config;
		_brokerProperties = "{\"PartitionKey\": \"" + config.app + "/" + config.version + "\"}";		
	}
	
	public EventWriter open() throws IOException {
		HttpsURLConnection conn = (HttpsURLConnection) ((new URL (_config.eventHubUrl).openConnection()));
		
		try {
			conn.setRequestMethod("POST");
		} catch (ProtocolException e) {
		}
		
		conn.setRequestProperty("Authorization", _config.eventHubAuthorization);
		conn.setRequestProperty("Content-Type", "application/atom+xml;type=entry;charset=utf-8");
		conn.setRequestProperty("BrokerProperties", _brokerProperties);
		conn.setUseCaches(false);
		conn.setDoInput(false);
		conn.setDoOutput(true);
		//conn.setConnectTimeout(10000);
		
		conn.connect();
		
		return new EventWriter(conn, conn.getOutputStream());		
	}
}
