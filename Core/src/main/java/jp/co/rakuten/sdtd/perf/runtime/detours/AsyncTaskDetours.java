package jp.co.rakuten.sdtd.perf.runtime.detours;

import android.os.AsyncTask;
import jp.co.rakuten.sdtd.perf.runtime.annotations.DetourStaticCall;
import jp.co.rakuten.sdtd.perf.runtime.wrappers.RunnableWrapper;

public class AsyncTaskDetours {

	@DetourStaticCall
	public static void execute(Runnable runnable) {
		AsyncTask.execute(new RunnableWrapper(runnable));
	}
}