package com.rakuten.tech.mobile.perf.core.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.View;

import com.rakuten.tech.mobile.perf.core.Tracker;
import com.rakuten.tech.mobile.perf.core.TrackerImpl;
import com.rakuten.tech.mobile.perf.core.annotations.MinCompileSdkVersion;

public class ActivityBase extends Activity {

	public boolean com_rakuten_tech_mobile_perf_onCreate_tracking;
	private final String activityName = this.getClass().getName();

	protected void onCreate (Bundle savedInstanceState) {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		super.onCreate(savedInstanceState);
	}

	@MinCompileSdkVersion(21)
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		super.onCreate(savedInstanceState, persistentState);
	}

	public View onCreateView(String name, Context context, AttributeSet attrs) {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		return super.onCreateView(name, context, attrs);
	}

	@MinCompileSdkVersion(11)
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		return super.onCreateView(parent, name, context, attrs);
	}

	protected void onStart () {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		super.onStart();
	}

	protected void onStop () {
		Tracker.prolongMetric();
		super.onStop();
	}

	protected void onResume () {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		super.onResume();
	}

	protected void onPause () {
		Tracker.prolongMetric();
		super.onPause();
	}

	protected void onRestart () {
		TrackerImpl.activityName = activityName;
		Tracker.prolongMetric();
		super.onRestart();
	}

	protected void onDestroy () {
		Tracker.prolongMetric();
		super.onDestroy();
	}

	@MinCompileSdkVersion(5)
	public void onBackPressed () {
		Tracker.endMetric();
		super.onBackPressed();
	}

	@MinCompileSdkVersion(3)
	public void onUserInteraction () {
		Tracker.endMetric();
		super.onUserInteraction();
	}
}
