package jp.co.rakuten.sdtd.perf.runtime.mixins;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import jp.co.rakuten.sdtd.perf.runtime.Tracker;
import jp.co.rakuten.sdtd.perf.runtime.annotations.MixImplementationOf;
import jp.co.rakuten.sdtd.perf.runtime.annotations.ReplaceMethod;

@MixImplementationOf(OnItemClickListener.class)
public class AdapterViewOnItemClickListenerMixin {

	@ReplaceMethod
	public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
		int id = Tracker.startUI(view, "onItemClick");
		try {
			onItemClick(parent, view, position, itemId);
		}
		finally {
			Tracker.endUI(id);
		}
	}
}
