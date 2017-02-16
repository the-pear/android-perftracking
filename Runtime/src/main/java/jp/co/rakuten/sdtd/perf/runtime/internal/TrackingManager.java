package jp.co.rakuten.sdtd.perf.runtime.internal;

/**
 * TrackingManager
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
public class TrackingManager {
    public static TrackingManager INSTANCE = new TrackingManager();

    private TrackingManager() {
    }

    /**
     * Starts a new measurement.
     *
     * @param id Measurement identifier.
     */
    public void startMeasurement(String id) {

    }

    /**
     * Ends a measurement.
     *
     * @param id Measurement identifier.
     */
    public void endMeasurement(String id) {

    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void startAggregated(String id, Object object) {

    }

    /**
     * Ends a measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement. This must be the same
     *               object that got passed to startAggregated().
     */
    public void endAggregated(String id, Object object) {

    }

    /**
     * Starts a new metric.
     *
     * @param id Measurement identifier.
     */
    public void startMetric(String id) {

    }
}
