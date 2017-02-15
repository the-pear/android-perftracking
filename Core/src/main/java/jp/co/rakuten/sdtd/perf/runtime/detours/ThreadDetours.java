package jp.co.rakuten.sdtd.perf.runtime.detours;

import jp.co.rakuten.sdtd.perf.runtime.annotations.DetourConstructorParameter;
import jp.co.rakuten.sdtd.perf.runtime.wrappers.RunnableWrapper;

public class ThreadDetours {

	@DetourConstructorParameter(Thread.class)
	public static Runnable wrapRunnableForThread(Runnable runnable) {
		return new RunnableWrapper(runnable);
	}
}
