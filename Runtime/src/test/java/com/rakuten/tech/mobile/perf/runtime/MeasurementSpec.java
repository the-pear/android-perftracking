package com.rakuten.tech.mobile.perf.runtime;

import com.rakuten.tech.mobile.perf.runtime.internal.TrackingManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.util.Pair;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.verify;


public class MeasurementSpec extends RobolectricUnitSpec {
    @Mock TrackingManager trackingManager;

    @Before public void init() {
        TrackingManager.INSTANCE = trackingManager;
    }

    // input validations

    @RunWith(ParameterizedRobolectricTestRunner.class)
    public static class InvalidInputSpec extends RobolectricUnitSpec {
        public enum Action { START, END, START_AGGREGATED, END_AGGREGATED };
        @ParameterizedRobolectricTestRunner.Parameters(name = "Action = {0}, Input = {1}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {Action.START,  null},
                    {Action.START,  ""},
                    {Action.END,    null},
                    {Action.END,    ""},
                    {Action.START_AGGREGATED,   Pair.create(null, null)},
                    {Action.START_AGGREGATED,   Pair.create("", null)},
                    {Action.START_AGGREGATED,   Pair.create("valid", null)},
                    {Action.END_AGGREGATED,     Pair.create(null, null)},
                    {Action.END_AGGREGATED,     Pair.create("", null) },
                    {Action.END_AGGREGATED,     Pair.create("valid", null) }
            });
        }

        private String input1;
        private Comparable input2;
        private Action action;

        public InvalidInputSpec(Action action, Object input) {
            if(input instanceof Pair) {
                this.input1 = (String) ((Pair) input).first;
                this.input2 = (Comparable) ((Pair) input).second;
            } else {
                this.input1 = (String) input;
            }
            this.action = action;
        }

        @Test(expected = IllegalArgumentException.class)
        public void shouldValidateIdIsNotNullOnStart() {
            switch (action) {
                case START:             Measurement.start(input1); break;
                case END:               Measurement.end(input1); break;
                case START_AGGREGATED:  Measurement.startAggregated(input1, input2); break;
                case END_AGGREGATED:    Measurement.endAggregated(input1, input2); break;
            }
            ;
        }
    }

    @Test public void shouldRelayStartToTrackingManager() {
        Measurement.start("validId");
        verify(trackingManager).startMeasurement("validId");
    }

    // valid input

    @Test public void shouldRelayEndToTrackingManager() {
        Measurement.start("validId");
        Measurement.end("validId");
        verify(trackingManager).startMeasurement("validId");
        verify(trackingManager).endMeasurement("validId");
    }

    @Test public void shouldRelayEndToTrackingManagerEvenIfNotStarted() {
        Measurement.end("validId");
        verify(trackingManager).endMeasurement("validId");
    }

    @Test public void shouldRelayStartAggregatedToTrackingManager() {
        Measurement.startAggregated("validId", "validObject");
        verify(trackingManager).startAggregated("validId", "validObject");
    }

    @Test public void shouldRelayEndAggregatedToTrackingManager() {
        Measurement.startAggregated("validId", "validObject");
        Measurement.endAggregated("validId", "validObject");
        verify(trackingManager).startAggregated("validId", "validObject");
        verify(trackingManager).endAggregated("validId", "validObject");
    }

    @Test public void shouldRelayEndAggregatedToTrackingManagerEvenIfNotStarted() {
        Measurement.endAggregated("validId", "validObject");
        verify(trackingManager).endAggregated("validId", "validObject");
    }

    // misbehavior of collaborators

    @Test public void shouldNotFailOnNullTrackingManager() {
        TrackingManager.INSTANCE = null;

        Measurement.start("id1");
        Measurement.end("id1");
        Measurement.startAggregated("id2", "val");
        Measurement.endAggregated("id2", "val");

        // no exception thrown
    }
}
