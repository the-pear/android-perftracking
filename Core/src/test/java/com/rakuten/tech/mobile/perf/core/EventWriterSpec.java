package com.rakuten.tech.mobile.perf.core;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
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
        when(conn.getResponseCode()).thenReturn(201);

        writer = new EventWriter(config, envInfo, url);
        assertThat(writer).isNotNull();
    }

    // creation & init

    @Test public void shouldOpenConnectionOnBegin() throws IOException {
        writer = new EventWriter(config, envInfo, null);

        writer.begin();

        // Verify no exception
    }

    @Test public void shouldDisconnectOnFailure() throws IOException {
        when(conn.getOutputStream()).thenThrow(new IOException());


        try {
            writer.begin();
        } catch (IOException ignored) {}

        verify(conn).disconnect();
    }

    @Test public void shouldFailSilentlyOnBadUrl() throws IOException {

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

    // writing

    @Rule public TestData emptyJson = new TestData("no_measurement.json");
    @Test public void shouldWriteConfigAndEnvInfo() throws IOException, JSONException {
        writer.begin();
        writer.end();

        String writtenJson = extractWrittenString(outputStream);
        JSONAssert.assertEquals(emptyJson.content, writtenJson, true);
    }

    @Rule public TestData emptyNoEnvJson = new TestData("no_measurement_no_env.json");
    @Test public void shouldHandleNullsInEnvInfo() throws IOException, JSONException {
        envInfo.country = null;
        envInfo.device = null;
        envInfo.network = null;

        writer.begin();
        writer.end();

        String writtenJson = extractWrittenString(outputStream);
        JSONAssert.assertEquals(emptyNoEnvJson.content, writtenJson, true);
    }

    @Rule public TestData metricJson = new TestData("metric.json");
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
        JSONAssert.assertEquals(metricJson.content, writtenString, true);
    }

    @Rule public TestData methodMeasurementJson = new TestData("method_measurement.json");
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
        JSONAssert.assertEquals(methodMeasurementJson.content, writtenString, true);
    }

    @Rule public TestData urlMeasurementJson = new TestData("url_measurement.json");
    @Test public void shouldWriteUrlObjectMeasurement() throws JSONException, IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = new URL("https://rakuten.co.jp/some/path?and=some&url=params");
        measurement.b = "VERB";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(measurement, "test-metric");
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(urlMeasurementJson.content, writtenString, true);
    }

    @Test public void shouldWriteUrlStringMeasurement() throws JSONException, IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = "https://rakuten.co.jp/some/path?and=some&url=params";
        measurement.b = "VERB";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(measurement, "test-metric");
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(urlMeasurementJson.content, writtenString, true);
    }

    @Rule public TestData customMeasurementJson = new TestData("custom_measurement.json");
    @Test public void shouldWriteSingleCustomMeasurement() throws JSONException, IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.CUSTOM;
        measurement.a = "custom-measurement";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(measurement, "test-metric");
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(customMeasurementJson.content, writtenString, true);
    }

    // silent exception handling

    @Test public void shouldNotFailOnBadUrl() {
        config.eventHubUrl = "usnteaueau";
        writer = new EventWriter(config, envInfo);
        // no exceptions
    }

    @Test public void shouldNotFailOnGoodUrl() {
        config.eventHubUrl = "https://rakuten.co.jp";
        writer = new EventWriter(config, envInfo);
        // no exceptions
    }

    @Test public void shouldNotFailOnWritingNull() throws IOException {
        writer.write(null);
        writer.write(null, null);

        writer.begin();
        writer.write(null);
        writer.write(null, null);
        writer.end();
        // no exceptions
    }

    @Test public void shouldNotFailOnWriteWithoutBegin() throws IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.CUSTOM;
        measurement.a = "";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;
        writer.write(measurement, "");
        // no exceptions
    }

    @Test public void shouldNotFailOnNullPayload() throws IOException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.CUSTOM;
        measurement.a = null;
        measurement.b = null;
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;
        writer.begin();
        writer.write(measurement, null);
        measurement.type = Measurement.METHOD;
        writer.write(measurement, null);
        measurement.type = Measurement.URL;
        writer.write(measurement, null);
        measurement.type = Measurement.METRIC;
        writer.write(measurement, null);
        measurement.type = 5; // invalid type
        writer.write(measurement, null);

        Metric metric = new Metric();
        metric.startTime = 0L;
        metric.endTime = 999 * 1000000L;
        metric.id = null;
        metric.urls = 999;
        writer.write(metric);
        writer.end();
        // no exceptions
    }

    @Test public void shouldNotFailOnIncorrectEnd() throws IOException {
        writer.end();
        writer.begin();
        writer.end();
        writer.end();
        // no exceptions
    }

    @Rule public TestData escapedJson = new TestData("escaped.json");
    @Test public void shouldHandleMalformedURLData() throws IOException, JSONException {
        Measurement measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = new URL("http://example.com:80/page1\".html");
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.begin();
        writer.write(measurement, "test-metric");
        measurement.a = new URL("http://example.com:80/page1\"[.html");
        writer.write(measurement, "test-metric");
        measurement.a = new URL("http://example.com:80/page1\"{.html");
        writer.write(measurement, "test-metric");
        measurement.a = new URL("http://example.com:80/page1\",.html");
        writer.write(measurement, "test-metric");
        measurement.a = new URL("http://example.com:80/page1'}'.html");
        writer.write(measurement, "test-metric");
        //For URL as String.
        measurement.a = "http://example.com:80/page1\".html";
        writer.write(measurement, "test-metric");
        measurement.a = "http://example.com:80/page1\"[.html";
        writer.write(measurement, "test-metric");
        measurement.a = "http://example.com:80/page1\"{.html";
        writer.write(measurement, "test-metric");
        measurement.a = "http://example.com:80/page1\",.html";
        writer.write(measurement, "test-metric");
        measurement.a = "http://example.com:80/page1'}'.html";
        writer.write(measurement, "test-metric");
        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(escapedJson.content, writtenString, true);
    }


    // smoke test

    @Rule public TestData smokeTestJson = new TestData("smoke_test.json");
    @Test public void smokeTest() throws IOException, JSONException {
        Metric metric = new Metric();
        metric.startTime = 0L;
        metric.endTime = 999 * 1000000L;
        metric.id = "test-metric";
        metric.urls = 999;

        writer.begin();
        writer.write(metric);
        writer.write(metric);

        Measurement measurement = new Measurement();
        measurement.type = Measurement.METHOD;
        measurement.a = "TestClass";
        measurement.b = "testMethod";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.write(measurement, metric.id);

        measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = "https://rakuten.co.jp1/some/path?and=some&url=params";
        measurement.b = "VERB";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.write(measurement, metric.id);

        measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = "https://rakuten.co.jp2/some/path?and=some&url=params";
        measurement.b = "VERB";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.write(measurement, null);

        measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = "https://rakuten.co.jp3/some/path?and=some&url=params";
        measurement.b = null;
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.write(measurement, null);

        measurement = new Measurement();
        measurement.type = Measurement.CUSTOM;
        measurement.a = "custom-measurement";
        measurement.startTime = 0L;
        measurement.endTime = 999 * 1000000L;

        writer.write(measurement, metric.id);

        measurement = new Measurement();
        measurement.type = Measurement.URL;
        measurement.a = new URL("https://amazon.co.jp/other/path?and=some&url=params");
        measurement.b = "BERV";
        measurement.startTime = 0L;
        measurement.endTime = 100 * 1000000L;

        writer.write(measurement, metric.id);

        writer.end();

        String writtenString = extractWrittenString(outputStream);
        JSONAssert.assertEquals(smokeTestJson.content, writtenString, true);
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
