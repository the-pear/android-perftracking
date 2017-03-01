package jp.co.rakuten.sdtd.perf.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MeasurementBuffer {

	// Size must by a power of 2 so that Integer.MAX_VALUE is divisible by it
	public static final int SIZE = 512; 

	private final Measurement[] _buffer = new Measurement[SIZE];
	private final AtomicInteger _nextTrackingId = new AtomicInteger(1);
	
	public MeasurementBuffer() {
		for (int i = 0; i < SIZE; i++) {
			_buffer[i] = new Measurement();
		}
	}
	
	public Measurement next() {
		int id = _nextTrackingId.getAndIncrement();
		
		// Ensure id is not zero as zero has special purpose
		if (id == 0) {
			id = _nextTrackingId.getAndIncrement();
		}
		
		int index = id % SIZE;
		if (index < 0) {
			index += SIZE;
		}
		
		Measurement m = _buffer[index];
		m.clear();
		m.trackingId = id;
		
		return m;
	}
	
	public Measurement get(int trackingId) {
		int index = trackingId % SIZE;
		if (index < 0) {
			index += SIZE;
		}
		
		Measurement m = _buffer[index];
		if (m.trackingId == trackingId)
		{
			return m;
		}
		
		return null;
	}
	
	public void flush(MeasurementProcessor processor) throws IOException {
		int index = _nextTrackingId.get() % SIZE;
		if (index < 0) {
			index += SIZE;
		}
		
		for (int i = 0; i < SIZE; i++) {
			if (index != 0)
			{
				Measurement m = _buffer[index];
				int trackingId = m.trackingId;
				if ((trackingId != 0) && (m.endTime != 0)) {
					processor.process(trackingId);
					m.clear();
				}
			}
			index = (index + 1) % SIZE;
		}
	}
}
