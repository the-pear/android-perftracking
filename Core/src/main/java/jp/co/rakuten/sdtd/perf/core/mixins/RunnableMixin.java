package jp.co.rakuten.sdtd.perf.core.mixins;

import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.MixImplementationOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;

@MixImplementationOf(Runnable.class)
public class RunnableMixin {

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