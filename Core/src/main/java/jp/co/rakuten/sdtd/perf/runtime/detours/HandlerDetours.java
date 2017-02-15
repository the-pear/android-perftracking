package jp.co.rakuten.sdtd.perf.runtime.detours;

import android.os.Handler;
import jp.co.rakuten.sdtd.perf.runtime.annotations.DetourCall;
import jp.co.rakuten.sdtd.perf.runtime.wrappers.RunnableWrapper;

public class HandlerDetours {

	@DetourCall
	public static boolean post(Handler handler, Runnable runnable) {
		return handler.post(new RunnableWrapper(runnable));
	}
	
	@DetourCall
	public static boolean postAtFrontOfQueue(Handler handler, Runnable runnable) {
		return handler.postAtFrontOfQueue(new RunnableWrapper(runnable));
	}

	@DetourCall
	public static boolean postAtTime(Handler handler, Runnable runnable, long uptimeMillis) {
		return handler.postAtTime(new RunnableWrapper(runnable), uptimeMillis);
	}
	
	@DetourCall
	public static boolean postAtTime(Handler handler, Runnable runnable, Object token, long uptimeMillis) {
		return handler.postAtTime(new RunnableWrapper(runnable), token, uptimeMillis);
	}

	@DetourCall
	public static boolean postDelayed(Handler handler, Runnable runnable, long delayMillis) {
		return handler.postDelayed(new RunnableWrapper(runnable), delayMillis);
	}
}

