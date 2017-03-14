package jp.co.rakuten.sdtd.perf.runtime;

import android.text.TextUtils;

import jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager;

/**
 * Measurement
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public final class Measurement {
    /**
     * Starts a new measurement.
     *
     * @param measurementId Measurement identifier.
     * @return trackingId
     * @see #end(String)
     */
    public static void start(String measurementId) {
        if (TextUtils.isEmpty(measurementId))
            throw new IllegalArgumentException("Illegal Arguments");
            TrackingManager.INSTANCE.startMeasurement(measurementId);
    }

    /**
     * Ends a measurement.
     *
     * @param measurementId Measurement identifier.
     * @see #start(String)
     */
    public static void end(String measurementId) {
        if (TextUtils.isEmpty(measurementId))
            throw new IllegalArgumentException("Illegal Argument");
            TrackingManager.INSTANCE.endMeasurement(measurementId);
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     * @see #endAggregated(String, Comparable)
     */
    public static void startAggregated(String id, Comparable object) {
        if (TextUtils.isEmpty(id) || object == null)
            throw new IllegalArgumentException("Illegal Arguments");
            TrackingManager.INSTANCE.startAggregated(id, object);
    }

    /**
     * Ends a aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement. This must be the same
     *               object that got passed to startAggregated().
     * @see #startAggregated(String, Comparable)
     */
    public static void endAggregated(String id, Comparable object) {
        if (TextUtils.isEmpty(id) || object == null)
            throw new IllegalArgumentException("Illegal Arguments");
            TrackingManager.INSTANCE.endAggregated(id, object);
    }
}
