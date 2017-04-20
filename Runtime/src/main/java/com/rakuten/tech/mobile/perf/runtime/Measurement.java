package com.rakuten.tech.mobile.perf.runtime;

import android.text.TextUtils;
import android.util.Log;

import com.rakuten.tech.mobile.perf.runtime.internal.TrackingManager;

/**
 * Measurement
 *
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Measurement {

    private static final String TAG = Measurement.class.getSimpleName();
    /**
     * Starts a new measurement.
     *
     * @param measurementId Measurement identifier.
     * @return trackingId
     * @see #end(String)
     */
    public static void start(String measurementId) {
        if (TextUtils.isEmpty(measurementId)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.startMeasurement(measurementId);
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
    }

    /**
     * Ends a measurement.
     *
     * @param measurementId Measurement identifier.
     * @see #start(String)
     */
    public static void end(String measurementId) {
        if (TextUtils.isEmpty(measurementId)) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.endMeasurement(measurementId);
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     * @see #endAggregated(String, Comparable)
     */
    public static void startAggregated(String id, Comparable object) {
        if (TextUtils.isEmpty(id) || object == null) {
            throw new IllegalArgumentException("Illegal Arguments");
        }
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.startAggregated(id, object);
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
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
        if (TextUtils.isEmpty(id) || object == null) {
            throw new IllegalArgumentException("Illegal Arguments");
        }
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.endAggregated(id, object);
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
    }

}
