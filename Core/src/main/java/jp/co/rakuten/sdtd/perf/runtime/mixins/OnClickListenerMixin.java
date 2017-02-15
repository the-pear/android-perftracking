package jp.co.rakuten.sdtd.perf.runtime.mixins;

import android.view.View;
import android.view.View.OnClickListener;
import jp.co.rakuten.sdtd.perf.runtime.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.annotations.MixImplementationOf;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ReplaceMethod;

@MixImplementationOf(OnClickListener.class)
public class OnClickListenerMixin {

	@ReplaceMethod
	public void onClick(View view) {
		int id = Tracker.startUI(view, "onClick");
		try {
			onClick(view);
		}
		finally {
			Tracker.endUI(id);
		}
	}
}
