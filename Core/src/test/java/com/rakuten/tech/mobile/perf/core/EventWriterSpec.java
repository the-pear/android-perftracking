package com.rakuten.tech.mobile.perf.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.portable.OutputStream;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventWriterSpec {
    Config config;
    EnvironmentInfo envInfo;
    @Mock URL url;
    @Mock OutputStream outputStream;
    @Mock HttpsURLConnection conn;
    private EventWriter writer;

    @Before public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        config = new Config();
        config.app = "app";
        config.debug = true;
        config.eventHubUrl = ""; // url injected via constructor
        config.header = new HashMap<>();

        envInfo = new EnvironmentInfo();
        envInfo.country = "test-land";
        envInfo.network = "test-network";
        envInfo.device = "test-device";

        when(url.openConnection()).thenReturn(conn);
        when(conn.getOutputStream()).thenReturn(outputStream);

        writer = new EventWriter(config, envInfo, url);
        assertThat(writer).isNotNull();
    }

    @Test public void shouldOpenConnectionOnBegin() throws IOException {
        writer.begin();
        verify(url).openConnection();
        verify(conn).getOutputStream();
    }

    @Test public void shouldConfigureConnectionOnBegin() throws IOException {
        config.header.put("test-header", "test-header-value");
        writer.begin();
        verify(conn).setRequestMethod("POST");
        verify(conn).setRequestProperty("test-header", "test-header-value");
        verify(conn).setUseCaches(false);
        verify(conn).setDoInput(false);
        verify(conn).setDoOutput(true);
    }
}
