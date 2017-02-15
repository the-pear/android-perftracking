package jp.co.rakuten.sdtd.perf.runtime;

import java.util.HashMap;

import android.util.Log;

public class _SdkMeasurement 
{
	private int _trackingId;
	
	private static final HashMap<String, _SdkMeasurement> _inProgress = new HashMap<String, _SdkMeasurement>(); 
	
	public synchronized static void start(String id) {
		_SdkMeasurement m = _inProgress.get(id);
		
		if (m != null)
		{
			Log.d("PERF", "Measurement '" + id + "' already in progress");
			return;
		}
		
		m = new _SdkMeasurement();
		m._trackingId = Tracker.startCustom(id);
		_inProgress.put(id, m);
	}
	
	public synchronized static void end(String id) {
		_SdkMeasurement m = _inProgress.get(id);
		
		if (m == null)
		{
			Log.d("PERF", "No '" + id + "' measurement in progress");
			return;
		}
		
		Tracker.endCustom(m._trackingId);
		_inProgress.remove(id);
	}
}
