package com.rakuten.tech.mobile.perf.core;

public class SenderThread extends Thread {
	private static final int MIN_COUNT = 10;
	private static final int SLEEP_INTERVAL_MILLISECONDS = 10000;

	private final MeasurementBuffer _buffer;
	private final Sender _sender;

	public SenderThread(MeasurementBuffer buffer, Sender sender) {
		_buffer = buffer;
		_sender = sender;
	}

	@Override
	public void run() {
		int index = 1;

		while (true) {
			int idIndex = _buffer.nextTrackingId.get() % MeasurementBuffer.SIZE;
			if (idIndex < 0) {
				idIndex += MeasurementBuffer.SIZE;
			}

			int count = idIndex - index;
			if (count < 0) {
				count += MeasurementBuffer.SIZE;
			}

			if (count >= MIN_COUNT) {
				index = _sender.send(index, idIndex);
			}

			try {
				Thread.sleep(SLEEP_INTERVAL_MILLISECONDS);
			}
			catch (InterruptedException e) {
			}
		}
	}
}
