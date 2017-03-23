package jp.co.rakuten.sdtd.perf.core.mixins;

import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;

@MixSubclassOf(Thread.class)
public class ThreadMixin {

    @ReplaceMethod
    public void run() {
        int id = Tracker.startMethod(this, "run");
        try {
            run();
        }
        finally {
            Tracker.endMethod(id);
        }
    }
}