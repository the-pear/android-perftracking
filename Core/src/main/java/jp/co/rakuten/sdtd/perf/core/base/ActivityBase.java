package jp.co.rakuten.sdtd.perf.core.base;

import android.app.Activity;
import android.os.Bundle;
import jp.co.rakuten.sdtd.perf.core.Tracker;

public class ActivityBase extends Activity {

	public boolean jp_co_rakuten_sdtd_perf_onCreate_tracking;

	public void onCreate (Bundle savedInstanceState) {
		Tracker.prolongMetric();
		super.onCreate(savedInstanceState);
	}

	public void onStart () {
		Tracker.prolongMetric();
		super.onStart();
	}

	public void onStop () {
		Tracker.prolongMetric();
		super.onStop();
	}

	public void onResume () {
		Tracker.prolongMetric();
		super.onResume();
	}

	public void onPause () {
		Tracker.prolongMetric();
		super.onPause();
	}

	public void onRestart () {
		Tracker.prolongMetric();
		super.onRestart();
	}

	public void onDestroy () {
		Tracker.prolongMetric();
		super.onDestroy();
	}

	public void onBackPressed () {
		Tracker.endMetric();
		super.onBackPressed();
	}
}
