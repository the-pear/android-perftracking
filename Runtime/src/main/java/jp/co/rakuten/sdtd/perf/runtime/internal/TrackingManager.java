package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import jp.co.rakuten.sdtd.perf.core.Config;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.Measurement;

/**
 * TrackingManager
 *
 * @author RMSDK team(prj-rmsdk@mail.rakuten.com)
 */
public class TrackingManager {
    private static final String TAG = TrackingManager.class.getSimpleName();
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
     */
    public synchronized void startMeasurement(String measurementId) {
        TrackingData trackingData = new TrackingData(measurementId, null);
        if (mTrackingData.size() >= TRACKING_DATA_LIMIT)
            mTrackingData.clear();
        if (!mTrackingData.containsKey(trackingData))
            mTrackingData.put(trackingData, Tracker.startCustom(measurementId));
        else
            Log.d(TAG, "Measurement already started");
    }

    /**
     * Ends a measurement.
     *
     * @param measurementId Measurement identifier.
     */
    public synchronized void endMeasurement(String measurementId) {
        TrackingData trackingData = new TrackingData(measurementId, null);
        if (mTrackingData.containsKey(trackingData)) {
            Tracker.endCustom(mTrackingData.get(trackingData));
            mTrackingData.remove(trackingData);
        } else
            Log.d(TAG, "Measurement not found");
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
        else
            Log.d(TAG, "Aggregated Measurement already started");
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
        } else
            Log.d(TAG, "Aggregated Measurement not found");
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
                if (measurementId.equals(data.measurementId))
                    return nullSafeEquateObjects(object, data.object);
                else
                    return false;
            } else
                return false;
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
                if (measurementId.compareTo(data.measurementId) == 0)
                    return nullSafeCompareObjects(object, data.object);
                else
                    return measurementId.compareTo(data.measurementId);
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
