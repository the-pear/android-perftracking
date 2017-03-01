package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Map<AggregatedData, List<Integer>> hashmap;

    private TrackingManager() {
        hashmap = new HashMap<>();
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
    public int startMeasurement(String measurementId) {
        return Tracker.startCustom(measurementId);
    }

    /**
     * Ends a measurement.
     *
     * @param trackingId Tracking ID returned from startCustom
     */
    public void endMeasurement(int trackingId) {
        Tracker.endCustom(trackingId);
    }

    /**
     * Starts a new aggregated measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void startAggregated(String id, Object object) {
        AggregatedData key = new AggregatedData(id, object);
        if (!hashmap.containsKey(key))
            hashmap.put(key, new ArrayList<Integer>(Tracker.startCustom(id)));
        else {
            List<Integer> data = hashmap.get(key);
            data.add(Tracker.startCustom(id));
            hashmap.put(key, data);
        }
    }

    /**
     * Ends a measurement.
     *
     * @param id     Measurement identifier.
     * @param object Object associated with the measurement.
     */
    public void endAggregated(String id, Object object) {
        AggregatedData key = new AggregatedData(id, object);
        if (hashmap.containsKey(key)) {
            List<Integer> trackingIdList = hashmap.get(key);
            Tracker.endCustom(trackingIdList.get(0));
            trackingIdList.remove(0);
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

    private class AggregatedData implements Comparable {
        private String measurementId;
        private Object object;

        private AggregatedData(String measurementId, Object object) {
            this.measurementId = measurementId;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AggregatedData) {
                AggregatedData data = (AggregatedData) o;
                return data.measurementId.equals(measurementId) && data.object.equals(object);
            } else return false;
        }

        @Override
        public int hashCode() {
            return measurementId.hashCode() + object.hashCode();
        }

        @Override
        public int compareTo(@NonNull Object another) {
            if (another instanceof AggregatedData) {
                AggregatedData data = (AggregatedData) another;
                return data.measurementId.compareTo(measurementId);
            }
            return -1;
        }
    }
}
