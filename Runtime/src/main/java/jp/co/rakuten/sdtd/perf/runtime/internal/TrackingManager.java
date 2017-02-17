package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.Context;

import jp.co.rakuten.sdtd.perf.core.Config;
import jp.co.rakuten.sdtd.perf.core.Tracker;

/**
 * TrackingManager
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
public class TrackingManager {
    public static TrackingManager INSTANCE = null;

    private TrackingManager() {
    }

    public static TrackingManager getInstance(Context context, Config config) {
        if (INSTANCE == null) {
            Tracker.on(context, config);
            INSTANCE = new TrackingManager();
            return INSTANCE;
        } else return INSTANCE;
    }

    /**
     * Starts a new measurement.
     *
     * @param measurementId Measurement identifier.
     */
    public void startMeasurement(String measurementId) {
        Tracker.startCustom(measurementId);
    }

    /**
     * Ends a measurement.
     *
     * @param trackingId Tracking ID returned from startCustom
     */
    public void endMeasurement(int trackingId) {
        Tracker.endCustom(trackingId);
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void startAggregated(String id, Object object) {
        Tracker.startMethod(object, id);
    }

    /**
     * Ends a measurement.
     *
     * @param trackingId Tracking ID returned from startMethod
     */
    public void endAggregated(int trackingId) {
        Tracker.endMethod(trackingId);
    }

    /**
     * Starts a new metric.
     *
     * @param metricId Metric ID, for example "launch", "search", "item"
     */
    public void startMetric(String metricId) {
        Tracker.startMetric(metricId);
    }
}
