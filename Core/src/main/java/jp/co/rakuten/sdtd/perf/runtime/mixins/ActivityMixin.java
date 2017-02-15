package jp.co.rakuten.sdtd.perf.runtime.mixins;

import android.app.Activity;
import android.os.Bundle;
import jp.co.rakuten.sdtd.perf.runtime.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.runtime.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.runtime.base.ActivityBase;

@MixSubclassOf(Activity.class)
@ChangeBaseTo(ActivityBase.class)
public class ActivityMixin extends ActivityBase {
	
	@ReplaceMethod
	protected void onCreate(Bundle savedInstanceState) {
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
}
