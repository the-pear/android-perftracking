package com.rakuten.tech.mobile.perf.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by avinash.renukaradhya on 17/04/17.
 */

public class SenderSpec {

    Sender sender;
    MeasurementBuffer measurementBuffer;
    Current current;
    @Mock EventWriter eventWriter;
    @Mock Debug debug;

    @Before public void init(){
        MockitoAnnotations.initMocks(this);
        measurementBuffer = new MeasurementBuffer();
    }

    @Test public void shouldSendMeasurements() {
        setUpMeasurement();
        Debug debug = new Debug();
        sender = new Sender(measurementBuffer, current, eventWriter, debug);
        sender.send(0);
        ArgumentCaptor<Measurement> captor = ArgumentCaptor.forClass(Measurement.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(10)).write(captor.capture(), (String) isNull());
        for (Measurement measurement: captor.getAllValues()) {
            assertEquals(measurement.a, null);// as measurements are cleared once sent.
        }
        verify(eventWriter, times(1)).end();
    }

    @Test public void shouldSendMetric() {
        setUpMetric();
        current = new Current();
        Debug debug = new Debug();
        sender = new Sender(measurementBuffer, current, eventWriter, debug);
        sender.send(0);
        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(9)).write(captor.capture());
        for (Metric metric: captor.getAllValues()) {
            assertEquals(metric.id, "custom-metric");
        }
        verify(eventWriter, times(1)).end();
    }

    @Test public void shouldSendMetricWithNegativeBuffer() {
        setUpMetric();
        current = new Current();
        measurementBuffer.nextTrackingId.set(-5);
        sender = new Sender(measurementBuffer, current, eventWriter, null);
        sender.send(0);
        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(eventWriter, times(1)).begin();
        verify(eventWriter, times(9)).write(captor.capture());
        for (Metric metric: captor.getAllValues()) {
            assertEquals(metric.id, "custom-metric");
        }
        verify(eventWriter, times(1)).end();
    }

    //Start index greater then buffer Max size
    @Test public void shouldNotSendMetric() {
        setUpMetric();
        current = new Current();
        measurementBuffer.nextTrackingId.set(513);
        sender = new Sender(measurementBuffer, current, eventWriter, null);
        sender.send(513);
        verify(eventWriter, never()).begin();
    }

    @Test public void shouldSendMeasurementsGreaterEndTime() {
        setUpMeasurementGreaterEndTime();
        sender = new Sender(measurementBuffer, current, eventWriter, debug);
        sender.send(0);
        verify(eventWriter, never()).begin();
    }

    @Test public void shouldNotSendMeasurementsAndMetric() {
        setUpMeasurementAndMetricGreaterEndTime();
        Debug debug = new Debug();
        current = new Current();
        current.metric.set((Metric) measurementBuffer.at[3].a);
        sender = new Sender(measurementBuffer, current, eventWriter, debug);
        sender.send(0);
        verify(eventWriter, times(1)).begin();
        ArgumentCaptor<Measurement> captor = ArgumentCaptor.forClass(Measurement.class);
        verify(eventWriter, times(1)).write(captor.capture(), (String) isNotNull());
        for (Measurement measurement: captor.getAllValues()) {
            assertEquals(measurement.a, null);// as measurements are cleared once sent.
        }
        verify(eventWriter, times(1)).end();
    }

    private void setUpMeasurement() {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 0L;
            measurement.endTime = 999 * 1000000L;
        }

    }

    private void setUpMeasurementGreaterEndTime() {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 999 * 1000000L;
            measurement.endTime = 0L;
        }

    }
    private void setUpMeasurementAndMetricGreaterEndTime() {
        for (int i = 0; i < 10; i++) {
            Measurement measurement = measurementBuffer.next();
            measurement.type = Measurement.CUSTOM;
            measurement.a = "custom-measurement";
            measurement.startTime = 999 * 1000000L;
            measurement.endTime = 1L;
            if(i == 2){
                measurement.type = Measurement.METRIC;
                Metric metric = new Metric();
                metric.id = "custom-metric";
                measurement.startTime = 0L;
                measurement.endTime = 999 * 1000000L;
                measurement.a = metric;
            }
            if(i == 3){
                measurement.type = Measurement.URL;
                Metric metric = new Metric();
                metric.id = "custom-url";
                measurement.startTime = 0L;
                measurement.endTime = 999 * 1000000L;
                measurement.a = metric;
            }
        }

    }

    private void setUpMetric() {
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
