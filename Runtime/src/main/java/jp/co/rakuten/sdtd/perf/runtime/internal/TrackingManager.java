package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import jp.co.rakuten.sdtd.perf.core.Config;
import jp.co.rakuten.sdtd.perf.core.Tracker;

/**
 * TrackingManager
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
public class TrackingManager {
    public static TrackingManager INSTANCE = null;
    private Map<TrackingData, Integer> mTrackingData;
    /* Max number of objects for @TrackingManager#mTrackingData */
    private static final int TRACKING_DATA_LIMIT = 100;

    private TrackingManager() {
        mTrackingData = new HashMap<>();
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
    public synchronized void startMeasurement(String measurementId) {
        TrackingData trackingData = new TrackingData(measurementId, null);
        if (mTrackingData.size() >= TRACKING_DATA_LIMIT)
            mTrackingData.clear();
        if(!mTrackingData.containsKey(trackingData))
            mTrackingData.put(trackingData, Tracker.startCustom(measurementId));
    }

    /**
     * Ends a measurement.
     *
     * @param measurementId Tracking ID returned from startCustom
     */
    public synchronized void endMeasurement(String measurementId) {
        TrackingData trackingData = new TrackingData(measurementId, null);
        if (mTrackingData.containsKey(trackingData)) {
            Tracker.endCustom(mTrackingData.get(trackingData));
            mTrackingData.remove(trackingData);
        }
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public synchronized void startAggregated(String id, Comparable object) {
        TrackingData key = new TrackingData(id, object);
        if (mTrackingData.size() >= TRACKING_DATA_LIMIT)
            mTrackingData.clear();
        if (!mTrackingData.containsKey(key))
            mTrackingData.put(key, Tracker.startCustom(id));
    }

    /**
     * Ends a aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public synchronized void endAggregated(String id, Comparable object) {
        TrackingData key = new TrackingData(id, object);
        if (mTrackingData.containsKey(key)) {
            Tracker.endCustom(mTrackingData.get(key));
            mTrackingData.remove(key);
        }
    }

    /**
     * Starts a new metric.
     *
     * @param metricId Metric ID, for example "launch", "search", "item"
     */
    public void startMetric(String metricId) {
        Tracker.startMetric(metricId);
    }

    private class TrackingData implements Comparable {
        private String measurementId;
        private Comparable object;

        private TrackingData(String measurementId, Comparable object) {
            this.measurementId = measurementId;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TrackingData) {
                TrackingData data = (TrackingData) o;
                return nullSafeEquateObjects(object, data.object) && measurementId.equals(data.measurementId);
            } else return false;
        }

        private boolean nullSafeEquateObjects(Comparable one, Comparable two) {
            if (one != null && two != null)
                return one.equals(two);
            if (one == null && two == null)
                return true;
            return false;
        }

        @Override
        public int hashCode() {
            if (object != null)
                return measurementId.hashCode() + object.hashCode();
            else
                return measurementId.hashCode();
        }

        @Override
        public int compareTo(@NonNull Object another) {
            if (another instanceof TrackingData) {
                TrackingData data = (TrackingData) another;
                return nullSafeCompareObjects(this.object, data.object) + data.measurementId.compareTo(measurementId) * 10000;
            }
            return -1;
        }


        private int nullSafeCompareObjects(Comparable one, Comparable two) {
            if (one == null ^ two == null)
                return (one == null) ? -1 : 1;
            if (one == null && two == null)
                return 0;
            return one.compareTo(two);
        }
    }
}
