package jp.co.rakuten.sdtd.perf.core.mixins;

import android.os.Bundle;
import android.app.Fragment;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.ChangeBaseTo;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;
import jp.co.rakuten.sdtd.perf.core.base.FragmentBase;

@MixSubclassOf(Fragment.class)
@ChangeBaseTo(FragmentBase.class)
public class FragmentMixin extends FragmentBase {

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
}

