package jp.co.rakuten.sdtd.perf.core;

public class SendTrigger {
	private static final int MIN_TO_SEND = 10;
	
	private int count;
	
	public void measurementEnded()
	{
		count++;
	}
	
	public boolean shouldSend()
	{
		return count >= MIN_TO_SEND;
	}
	
	public void sent()
	{
		count = 0;
	}
}
