package com.rakuten.tech.mobile.perf.runtime;

import android.util.Log;

import com.rakuten.tech.mobile.perf.runtime.internal.TrackingManager;
import com.rakuten.tech.mobile.perf.runtime.internal.Validation;

/**
 * Metric
 *
 */

public final class Metric {

    private static final String TAG = Metric.class.getSimpleName();
    /**
     * Starts a new metric.
     *
     * @param id Metric identifier.
     */
    public static void start(String id) {
        if (Validation.isInvalidId(id)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.startMetric(id);
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
    }
}
