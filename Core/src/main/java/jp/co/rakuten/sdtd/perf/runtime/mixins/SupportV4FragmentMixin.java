package jp.co.rakuten.sdtd.perf.runtime.mixins;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import jp.co.rakuten.sdtd.perf.runtime.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.runtime.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.runtime.base.SupportV4FragmentBase;

@MixSubclassOf(Fragment.class)
@ChangeBaseTo(SupportV4FragmentBase.class)
public class SupportV4FragmentMixin extends SupportV4FragmentBase {
	
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
	
	@ReplaceMethod
	protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (!jp_co_rakuten_sdtd_perf_onCreateView_tracking) {
			jp_co_rakuten_sdtd_perf_onCreateView_tracking = true;
			
			int id = Tracker.startMethod(this, "onCreateView");
			
			try {
				return onCreateView(inflater, container, savedInstanceState);
			}
			finally {
				Tracker.endMethod(id);
				jp_co_rakuten_sdtd_perf_onCreateView_tracking = false;
			}
		}
		
		return onCreateView(inflater, container, savedInstanceState);
	}
}
