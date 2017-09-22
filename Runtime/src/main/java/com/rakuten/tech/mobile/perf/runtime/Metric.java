package com.rakuten.tech.mobile.perf.runtime;

import android.util.Log;

import com.rakuten.tech.mobile.perf.runtime.internal.TrackingManager;
import com.rakuten.tech.mobile.perf.runtime.internal.Validation;

/**
 * Metric
 */

public final class Metric {

    private static final String TAG = Metric.class.getSimpleName();

    /**
     * Starts a new metric.
     *
     * @param id Metric identifier. Valid Arguments are AlphaNumeric, -, _, . and <i>Space</i>.
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

    /**
     * Prolongs current metric.
     *
     * By default the current metric will be prolonged and it keeps going with UI(Activity,
     * Fragment and Webview) life cycle events, in case once after you start a metric there is no
     * lifecycle event happened then your metric will not be recorded (As of know the minimum
     * duration for a metric to be recorded is 5 secs).
     *
     * So all metrics which are started by the user has to be manually prolonged by
     * calling `Metric#prolong()`.
     *
     * In parallel execution scenarios(Eg: mulitple image download) Metric should be prolonged in
     * each individual execution so as to capture exact download time.
     */
    public static void prolong() {
        if (TrackingManager.INSTANCE != null) {
            TrackingManager.INSTANCE.prolongMetric();
        } else {
            Log.d(TAG, "Tracking manager not initialized");
        }
    }
}
