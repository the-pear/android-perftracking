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

    static void initialize(Context context, Config config) {
        Tracker.on(context, config);
        INSTANCE = new TrackingManager();
    }

    /**
     * Starts a new measurement.
     *
     * @param measurementId Measurement identifier.
     * @return trackingId
     */
    public int startMeasurement(String measurementId) {
        return Tracker.startCustom(measurementId);
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
     * @return trackingId
     */
    public int startAggregated(String id, Object object) {
        return Tracker.startMethod(object, id);
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
