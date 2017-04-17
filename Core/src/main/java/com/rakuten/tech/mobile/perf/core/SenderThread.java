package com.rakuten.tech.mobile.perf.core;

public class SenderThread extends Thread {
	private static final int SLEEP_INTERVAL_MILLISECONDS = 10000;

	private final Sender _sender;

	public SenderThread(Sender sender) {
		_sender = sender;
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		int index = 1;

		while (true) {
			index = _sender.send(index);

			try {
				Thread.sleep(SLEEP_INTERVAL_MILLISECONDS);
			} catch (InterruptedException e) { /* continue looping if sleep is interrupted */ }
		}
	}
}
