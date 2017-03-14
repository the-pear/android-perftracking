package jp.co.rakuten.sdtd.perf.runtime;

import android.util.Log;

import jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager;

/**
 * Metric
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public final class Metric {

    private static final String TAG = Metric.class.getSimpleName();
    /**
     * Starts a new metric.
     *
     * @param id Metric identifier.
     */
    public static void start(String id) {
        if (TrackingManager.INSTANCE != null)
            TrackingManager.INSTANCE.startMetric(id);
        else
            Log.d(TAG,"Tracking manager not initialized");
    }
}
