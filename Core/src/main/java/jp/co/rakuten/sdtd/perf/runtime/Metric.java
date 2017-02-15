package jp.co.rakuten.sdtd.perf.runtime;

import java.util.concurrent.atomic.AtomicInteger;

public class Metric {
	public String id;
	public long startTime;
	public long unconfirmedEndTime;
	public final AtomicInteger measurementsInProgress = new AtomicInteger(0);
	public final AtomicInteger urls = new AtomicInteger(0);
}
