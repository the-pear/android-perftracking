package jp.co.rakuten.sdtd.perf.core.mixins;

import android.view.View;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.Exists;
import jp.co.rakuten.sdtd.perf.core.annotations.MixImplementationOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;

@Exists(OnItemClickListener.class)
@MixImplementationOf(OnItemClickListener.class)
public class SephirothAdapterViewOnItemClickListenerMixin {

    @ReplaceMethod
    public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
        Tracker.endMetric();

        int id = Tracker.startMethod(this, "onItemClick");
        try {
            onItemClick(parent, view, position, itemId);
        }
        finally {
            Tracker.endMethod(id);
        }
    }
}
