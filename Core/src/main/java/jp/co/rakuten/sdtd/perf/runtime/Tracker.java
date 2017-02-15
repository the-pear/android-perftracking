package jp.co.rakuten.sdtd.perf.runtime;

import java.net.URL;

import android.content.Context;
import android.view.View;

/**
* Tracker 
* 
* Abstracts and encapsulates low level performance tracking functionality.
* Intended to be used from instrumented code and Analytics SDK.
* Not intended to be used directly from app.
*/
public class Tracker {
	private static TrackerImpl _tracker;
	private static SenderThread _senderThread;

	/**
	   * Turns performance tracking on.
	   * @param context Instance of android.content.Context.
	   * @param config Performance tracking configuration.
	   */
    public static void on(Context context, Config config) {
    	MeasurementBuffer measurementBuffer = new MeasurementBuffer();
    	Snapshotter snapshotter = new Snapshotter(measurementBuffer);
    	SendTrigger sendTrigger = new SendTrigger();
    	_tracker = new TrackerImpl(measurementBuffer, sendTrigger, config.debug);
    	EventHub eventHub = new EventHub(config);
    	EnvironmentInfo envInfo = EnvironmentInfo.get(context);
    	Sender sender = new Sender(config, envInfo, eventHub, measurementBuffer, snapshotter, config.debug);
    	_senderThread = new SenderThread(sender, sendTrigger);
    	_senderThread.start();
    }
    
	/**
	   * Turns performance tracking off.
	   */
    public static void off()
    {
    	_tracker = null;
    	_senderThread = null;
    }

	/**
	   * Starts metric.
	   * @param metricId Metric ID, for example "launch", "search", "item".
	   */
	public static void startMetric(String metricId) {
		TrackerImpl t = _tracker;
		if (t != null) {
			t.startMetric(metricId);
		}
	}
	
	/**
	   * Starts method measurement.
	   * @param object Class instance
	   * @param method Method name
	   * @return Tracking ID
	   */
	public static int startMethod(Object object, String method) {
		TrackerImpl t = _tracker;
		return t != null ? t.startMethod(object, method) : 0;
	}

	/**
	   * Ends method measurement.
	   * @param trackingId Tracking ID returned from startMethod
	   */
	public static void endMethod(int trackingId) {
		TrackerImpl t = _tracker;
		if (t != null) {
			t.endMethod(trackingId);
		}
	}

	/**
	   * Starts URL measurement.
	   * @param url URL
	   * @param verb Verb, for example GET, POST, DELETE, or null
	   * @return Tracking ID
	   */
	public static int startUrl(URL url, String verb) {
		TrackerImpl t = _tracker;
		return t != null ? t.startUrl(url, verb) : 0;
	}

	/**
	   * Ends URL measurement.
	   * @param trackingId Tracking ID returned from startUrl
	   */
	public static void endUrl(int trackingId) {
		TrackerImpl t = _tracker;
		if (t != null) {
			t.endUrl(trackingId);
		}
	}

	/**
	   * Starts custom measurement.
	   * @param measurementId Measurement ID
	   * @return Tracking ID
	   */
	public static int startCustom(String measurementId) {
		TrackerImpl t = _tracker;
		return t != null ? t.startCustom(measurementId) : 0;
	}

	/**
	   * Ends custom measurement.
	   * @param trackingId Tracking ID returned from startCustom
	   */
	public static void endCustom(int trackingId) {
		TrackerImpl t = _tracker;
		if (t != null) {
			t.endCustom(trackingId);
		}
	}
	
	// These methods are likely to be removed
	
	public static int startUI(View view, String event) {
		return 0;
		//return _tracker != null ? _tracker.uiEventStart(view, event) : 0;
	}

	public static void endUI(int id) {
	//	if (_tracker != null) {
	//		_tracker.end(id);
	//	}
	}
}
