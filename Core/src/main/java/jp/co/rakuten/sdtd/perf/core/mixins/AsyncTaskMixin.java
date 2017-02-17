package jp.co.rakuten.sdtd.perf.core.mixins;

import android.os.AsyncTask;
import jp.co.rakuten.sdtd.perf.core.Tracker;
import jp.co.rakuten.sdtd.perf.core.annotations.MixSubclassOf;
import jp.co.rakuten.sdtd.perf.core.annotations.ReplaceMethod;

@MixSubclassOf(AsyncTask.class)
public class AsyncTaskMixin<Params, Progress, Result> {
	
	@ReplaceMethod
    protected Result doInBackground (Params... params) {
		int id = Tracker.startMethod(this, "doInBackground");
		try {
			return doInBackground(params);
		}
		finally {
			Tracker.endMethod(id);
		}
    }
}
