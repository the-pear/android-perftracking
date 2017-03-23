package jp.co.rakuten.sdtd.perf.core.mixins;

import android.app.Activity;
import android.os.Bundle;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.core.base.ActivityBase;

@MixSubclassOf(Activity.class)
@ChangeBaseTo(ActivityBase.class)
public class ActivityMixin extends ActivityBase {

	@ReplaceMethod
	public void onCreate(Bundle savedInstanceState) {
		if (!jp_co_rakuten_sdtd_perf_onCreate_tracking) {
			jp_co_rakuten_sdtd_perf_onCreate_tracking = true;

			int id = Tracker.startMethod(this, "onCreate");

			try {
				onCreate(savedInstanceState);
			}
			finally {
				Tracker.endMethod(id);
				jp_co_rakuten_sdtd_perf_onCreate_tracking = false;
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
