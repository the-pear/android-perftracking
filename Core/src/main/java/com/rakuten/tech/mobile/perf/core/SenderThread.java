package com.rakuten.tech.mobile.perf.core;

class SenderThread extends Thread {
	private static final int SLEEP_INTERVAL_MILLISECONDS = 10000;

	private final Sender _sender;
	private boolean isRunning;

	SenderThread(Sender sender) {
		_sender = sender;
		isRunning = true;
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		int index = 1;

		while (isRunning) {
			index = _sender.send(index);

			try {
				Thread.sleep(SLEEP_INTERVAL_MILLISECONDS);
			} catch (InterruptedException e) { /* continue looping if sleep is interrupted */ }
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void stopRunning() {
		isRunning = false;
	}
}
