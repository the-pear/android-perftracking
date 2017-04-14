package com.rakuten.tech.mobile.perf.core;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.portable.OutputStream;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventWriterSpec {
    private Config config;
    private EnvironmentInfo envInfo;
    @Mock URL url;
    @Mock OutputStream outputStream;
    @Mock HttpsURLConnection conn;
    private EventWriter writer;

    @Before public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        config = new Config();
        config.app = "app";
        config.version = "test-version";
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

    @Rule public TestResourceFile emptyJson = new TestResourceFile("no_measurement.json");
    @Test public void shouldWriteConfigAndEnvInfo() throws IOException, JSONException {
        writer.begin();
        writer.end();

        String writtenJson = extractWrittenString(outputStream);
        JSONAssert.assertEquals(emptyJson.content, writtenJson, true);
    }

    @Rule public TestResourceFile singleMetric = new TestResourceFile("single_metric.json");
    @Test public void shouldWriteSingleMetric() throws IOException, JSONException {
        Metric metric = new Metric();
        metric.startTime = 0L;
        metric.endTime = 999 * 1000000L;
        metric.id = "test-metric";
        metric.urls = 999;

        writer.begin();
        writer.write(metric);
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(singleMetric.content, writtenString, true);
    }

    @Rule public TestResourceFile singleMethodMeasurement =
            new TestResourceFile("single_method_measurement.json");
    @Test public void shouldWriteSingleMethodMeasurement() throws JSONException, IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.METHOD;
        measurement.a = "TestClass";
        measurement.b = "testMethod";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(measurement, "test-metric");
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(singleMethodMeasurement.content, writtenString, true);
    }

    @Rule public TestResourceFile mixMetricsAndMeasuremnts =
            new TestResourceFile("mix_metrics_measurements.json");
    @Test public void shouldWriteAMixOfMetricsAndMeasurements() throws IOException, JSONException {
        Metric metric = new Metric();
        metric.startTime = 0L;
        metric.endTime = 999 * 1000000L;
        metric.id = "test-metric";
        metric.urls = 999;

        Measurement measurement = new Measurement();
        measurement.type = Measurement.METHOD;
        measurement.a = "TestClass";
        measurement.b = "testMethod";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(metric);
        writer.write(measurement, metric.id);
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(mixMetricsAndMeasuremnts.content, writtenString, true);
    }

    // helpers

    private String extractWrittenString(OutputStream outputStream) throws IOException {
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        verify(outputStream).write(captor.capture(), anyInt(), anyInt());
        byte[] writtenBytes = captor.getValue();
        assertThat(writtenBytes).isNotEmpty();
        return new String(writtenBytes);
    }
}
