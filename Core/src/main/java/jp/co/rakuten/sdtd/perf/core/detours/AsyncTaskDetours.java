package jp.co.rakuten.sdtd.perf.core.detours;

import android.os.AsyncTask;
import jp.co.rakuten.sdtd.perf.core.annotations.DetourStaticCall;
import jp.co.rakuten.sdtd.perf.core.wrappers.RunnableWrapper;

public class AsyncTaskDetours {

	@DetourStaticCall
	public static void execute(Runnable runnable) {
		AsyncTask.execute(new RunnableWrapper(runnable));
	}
}