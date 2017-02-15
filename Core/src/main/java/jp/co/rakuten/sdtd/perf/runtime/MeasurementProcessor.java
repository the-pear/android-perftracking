package jp.co.rakuten.sdtd.perf.runtime;

import java.io.IOException;

public interface MeasurementProcessor {
	void process(int measurementId) throws IOException;
}
