package jp.co.rakuten.sdtd.perf.runtime;

import jp.co.rakuten.sdtd.perf.runtime.internal.TrackingManager;

/**
 * Metric
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */

public final class Metric {
    /**
     * Starts a new metric.
     *
     * @param id Metric identifier.
     */
    public static void start(String id) {
        TrackingManager.INSTANCE.startMetric(id);
    }
}
