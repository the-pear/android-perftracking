package com.rakuten.tech.mobile.perf.core.mixins;

import android.app.Activity;
import android.os.Bundle;
import com.rakuten.tech.mobile.perf.core.annotations.ReplaceMethod;
import com.rakuten.tech.mobile.perf.core.base.ActivityBase;
import com.rakuten.tech.mobile.perf.core.Tracker;
import com.rakuten.tech.mobile.perf.core.annotations.MixSubclassOf;

@MixSubclassOf(Activity.class)
public class ActivityMixin extends ActivityBase {

	@ReplaceMethod
	public void onCreate(Bundle savedInstanceState) {
		if (!com_rakuten_tech_mobile_perf_onCreate_tracking) {
			com_rakuten_tech_mobile_perf_onCreate_tracking = true;

			int id = Tracker.startMethod(this, "onCreate");

			try {
				onCreate(savedInstanceState);
			}
			finally {
				Tracker.endMethod(id);
				com_rakuten_tech_mobile_perf_onCreate_tracking = false;
			}
		}
		else {
			onCreate(savedInstanceState);
		}
	}

	@ReplaceMethod
	public void onBackPressed () {
		Tracker.endMetric();
		onBackPressed();
	}
}
