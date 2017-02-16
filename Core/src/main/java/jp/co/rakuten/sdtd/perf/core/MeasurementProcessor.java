package jp.co.rakuten.sdtd.perf.core;

import java.io.IOException;

public interface MeasurementProcessor {
	void process(int measurementId) throws IOException;
}
