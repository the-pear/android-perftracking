package com.rakuten.tech.mobile.perf.core;

import android.content.Context;

import java.net.URL;

/**
 * Tracker
 *
 * Abstracts and encapsulates low level performance tracking functionality.
 * Intended to be used from instrumented code and Analytics SDK.
 * Not intended to be used directly from app.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Tracker {
	private static TrackerImpl _tracker;
	private static SenderThread _senderThread;

	/**
	 * Turns performance tracking on.
	 * @param context Instance of android.content.Context.
	 * @param config Performance tracking configuration.
	 */
	public static void on(Context context, Config config) {
		Debug debug = config.debug ? new Debug() : null;
		MeasurementBuffer buffer = new MeasurementBuffer();
		Current current = new Current();
		_tracker = new TrackerImpl(buffer, current, debug);
		EnvironmentInfo envInfo = EnvironmentInfo.get(context);
		EventWriter writer = new EventWriter(config, envInfo);
		Sender sender = new Sender(buffer, current, writer, debug);
		_senderThread = new SenderThread(sender);
		_senderThread.start();
	}

	/**
	 * Turns performance tracking off.
	 */
    public static void off() {
        _tracker = null;
        SenderThread s = _senderThread;
        if(s != null) s.terminate();
        _senderThread = null;
    }

    /**
     * Starts metric.
     * @param metricId Metric ID, for example "launch", "search", "item".
     */
    public static void startMetric(String metricId) {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.startMetric(metricId);
            }
        } catch (Throwable t) {
            Tracker.off();
        }
    }

    /**
     * Prolongs current metric.
     */
    public static void prolongMetric() {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.prolongMetric();
            }
        } catch (Throwable t) {
            Tracker.off();
        }
    }

    /**
     * Terminates current metric.
     */
    public static void endMetric() {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.endMetric();
            }
        } catch (Throwable t) {
            Tracker.off();
        }
    }

    /**
     * Starts method measurement.
     * @param object Class instance
     * @param method Method name
     * @return Tracking ID
     */
    public static int startMethod(Object object, String method) {
        try {
            TrackerImpl t = _tracker;
            return t != null ? t.startMethod(object, method) : 0;
        } catch (Throwable t) {
            Tracker.off();
        }
        return 0;
    }

    /**
     * Ends method measurement.
     * @param trackingId Tracking ID returned from startMethod
     */
    public static void endMethod(int trackingId) {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.endMethod(trackingId);
            }
        } catch (Throwable t) {
            Tracker.off();
        }
    }

    /**
     * Starts URL measurement.
     * @param url URL
     * @param verb Verb, for example GET, POST, DELETE, or null
     * @return Tracking ID
     */
    public static int startUrl(URL url, String verb) {
        try {
            TrackerImpl t = _tracker;
            return t != null ? t.startUrl(url, verb) : 0;
        } catch (Throwable t) {
            Tracker.off();
        }
        return 0;
    }

    /**
     * Starts URL measurement.
     * @param url String
     * @param verb Verb, for example GET, POST, DELETE, or null
     * @return Tracking ID
     */
    public static int startUrl(String url, String verb) {
        try {
            TrackerImpl t = _tracker;
            return t != null ? t.startUrl(url, verb) : 0;
        } catch (Throwable t) {
            Tracker.off();
        }
        return 0;
    }

    /**
     * Ends URL measurement.
     * @param trackingId Tracking ID returned from startUrl
     */
    public static void endUrl(int trackingId) {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.endUrl(trackingId);
            }
        } catch (Throwable t) {
            Tracker.off();
        }
    }

    /**
     * Starts custom measurement.
     * @param measurementId Measurement ID
     * @return Tracking ID
     */
    public static int startCustom(String measurementId) {
        try {
            TrackerImpl t = _tracker;
            return t != null ? t.startCustom(measurementId) : 0;
        } catch (Throwable t) {
            Tracker.off();
        }
        return 0;
    }

    /**
     * Ends custom measurement.
     * @param trackingId Tracking ID returned from startCustom
     */
    public static void endCustom(int trackingId) {
        try {
            TrackerImpl t = _tracker;
            if (t != null) {
                t.endCustom(trackingId);
            }
        } catch (Throwable t) {
            Tracker.off();
        }
	}
}
