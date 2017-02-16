package jp.co.rakuten.sdtd.perf.core.detours;

import jp.co.rakuten.sdtd.perf.core.annotations.DetourConstructorParameter;
import jp.co.rakuten.sdtd.perf.core.wrappers.RunnableWrapper;

public class ThreadDetours {

	@DetourConstructorParameter(Thread.class)
	public static Runnable wrapRunnableForThread(Runnable runnable) {
		return new RunnableWrapper(runnable);
	}
}
