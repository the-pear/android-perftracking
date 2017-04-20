package com.rakuten.tech.mobile.perf.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SenderSpec {

    Sender sender;
    MeasurementBuffer measurementBuffer;
    Current current;
    @Mock
    EventWriter eventWriter;
    Debug debug;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        measurementBuffer = new MeasurementBuffer();
        debug = new Debug();
        current = new Current();
        sender = new Sender(measurementBuffer, current, eventWriter, debug);
    }

    @Test
    public void shouldSendMeasurements() {
        setUp10CustomMeasurement(measurementBuffer);
        sender.send(0);
        ArgumentCaptor<Measurement> captor = ArgumentCaptor.forClass(Measurement.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(10)).write(captor.capture(), (String) isNull());
        for (Measurement measurement : captor.getAllValues()) {
            assertThat(measurement.a).isNull();// as measurements are cleared once sent.
        }
        verify(eventWriter, times(1)).end();
    }

    @Test
    public void shouldSendMetric() {
        setUp10CustomMetric(measurementBuffer);
        sender.send(0);
        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(9)).write(captor.capture());
        for (Metric metric : captor.getAllValues()) {
            assertThat(metric.id).isEqualTo("custom-metric");
        }
        verify(eventWriter, times(1)).end();
    }

    @Test
    public void shouldSendMetricWithNegativeBuffer() {
        setUp10CustomMetric(measurementBuffer);
        measurementBuffer.nextTrackingId.set(-5);
        sender.send(0);
        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(9)).write(captor.capture());
        for (Metric metric : captor.getAllValues()) {
            assertThat(metric.id).isEqualTo("custom-metric");
        }
        verify(eventWriter, times(1)).end();
    }

    //Start index greater then buffer Max size
    @Test
    public void shouldNotSendMetricBufferSizeGreaterThenMax() {
        setUp10CustomMetric(measurementBuffer);
        measurementBuffer.nextTrackingId.set(513);
        sender.send(513);
        verify(eventWriter, never()).begin();
    }

    @Test
    public void shouldSendMeasurementsGreaterEndTime() {
        setUp10CustomMeasurementLesserEndTime(measurementBuffer);
        sender.send(0);
        verify(eventWriter, never()).begin();
    }

    @Test
    public void shouldNotSendMeasurementsAndMetric() {
        setUp10CustomMeasurementAndMetricLesserEndTime(measurementBuffer);
        current.metric.set((Metric) measurementBuffer.at[3].a);
        sender.send(0);
        verify(eventWriter, times(1)).begin();
        ArgumentCaptor<Measurement> captor = ArgumentCaptor.forClass(Measurement.class);
        verify(eventWriter, times(1)).write(captor.capture(), (String) isNotNull());
        for (Measurement measurement : captor.getAllValues()) {
            assertThat(measurement.a).isNull();// as measurements are cleared once sent.
        }
        verify(eventWriter, times(1)).end();
    }

    @Test
    public void shouldNotSendMetricLesserThenMaxTime() {
        setUp10CustomMetricLesserThenMaxTime(measurementBuffer);
        current.metric.set((Metric) measurementBuffer.at[3].a);
        sender.send(0);
        verify(eventWriter, never()).begin();
    }

    @Test
    public void shouldNotSendMeasurementsLesserThenMaxTime() {
        setUp10CustomMeasurementLesserThenMaxTime(measurementBuffer);
        sender.send(0);
        verify(eventWriter, never()).begin();
    }

    private void setUp10CustomMeasurement(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 0L;
            measurement.endTime = 999 * 1000000L;
        }
    }

    private void setUp10CustomMeasurementLesserThenMaxTime(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = System.nanoTime();
            measurement.endTime = 0L;
        }
    }

    private void setUp10CustomMeasurementLesserEndTime(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 999 * 1000000L;
            measurement.endTime = 0L;
        }
    }

    private void setUp10CustomMeasurementAndMetricLesserEndTime(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 999 * 1000000L;
            measurement.endTime = 1L;
            if (i == 2) {
                measurement.type = Measurement.METRIC;
                Metric metric = new Metric();
                metric.id = "custom-metric";
                measurement.startTime = 0L;
                measurement.endTime = 999 * 1000000L;
                measurement.a = metric;
            }
            if (i == 3) {
                measurement.type = Measurement.URL;
                Metric metric = new Metric();
                metric.id = "custom-url";
                measurement.startTime = 0L;
                measurement.endTime = 999 * 1000000L;
                measurement.a = metric;
            }
        }
    }

    private void setUp10CustomMetricLesserThenMaxTime(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 999 * 1000000L;
            ;
            measurement.endTime = 1L;
            if (i == 2) {
                measurement.type = Measurement.METRIC;
                Metric metric = new Metric();
                metric.id = "custom-metric";
                measurement.startTime = System.nanoTime();
                measurement.endTime = 999 * 1000000L;
                measurement.a = metric;
            }
        }
    }

    private void setUp10CustomMetric(MeasurementBuffer measurementBuffer) {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.METRIC;
            Metric metric = new Metric();
            metric.id = "custom-metric";
            metric.startTime = 0L;
            metric.endTime = 999 * 1000000L;
            measurement.a = metric;
        }
    }
}
