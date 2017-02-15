package jp.co.rakuten.sdtd.perf.runtime.wrappers;

import jp.co.rakuten.sdtd.perf.runtime.Tracker;

public class RunnableWrapper implements Runnable {
	
	private final Runnable _runnable;
	
	public RunnableWrapper(Runnable runnable) {
		_runnable = runnable;
	}

	@Override
	public void run() {
		if (_runnable != null) {
			int id = Tracker.startMethod(_runnable, "run");
			try {
				_runnable.run();
			}
			finally {
				Tracker.endMethod(id);
			}
		}
	}
}
