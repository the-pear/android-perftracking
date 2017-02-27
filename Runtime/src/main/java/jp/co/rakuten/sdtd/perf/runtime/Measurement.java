package jp.co.rakuten.sdtd.perf.runtime;

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
     * @param id Measurement identifier.
     * @return trackingId
     * @see #end(int)
     */
    public static int start(String id) {
        return TrackingManager.INSTANCE.startMeasurement(id);
    }

    /**
     * Ends a measurement.
     *
     * @param trackingId Measurement identifier.
     * @see #start(String)
     */
    public static void end(int trackingId) {
        TrackingManager.INSTANCE.endMeasurement(trackingId);
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     * @return trackingId
     * @see #endAggregated
     */
    public static int startAggregated(String id, Object object) {
        return TrackingManager.INSTANCE.startAggregated(id, object);
    }

    /**
     * Ends a measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement. This must be the same
     *               object that got passed to startAggregated().
     * @see #startAggregated(String, Object)
     */
    public static void endAggregated(String id, Object object) {
        TrackingManager.INSTANCE.endAggregated(id, object);
    }
}
