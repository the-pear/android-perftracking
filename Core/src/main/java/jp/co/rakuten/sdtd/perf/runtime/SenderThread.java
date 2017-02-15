package jp.co.rakuten.sdtd.perf.runtime;

import java.io.IOException;

public class SenderThread extends Thread {
	private static final int POLL_INTERVAL = 5000;
	
	private final Sender _sender;
	private final SendTrigger _trigger;
	
	public SenderThread(Sender sender, SendTrigger trigger) {
		_sender = sender;
		_trigger = trigger;
	}
	
	@Override
	public void run() {
		while (true) {
			if (_trigger.shouldSend()) {
				
				try {
					_sender.send();
					_trigger.sent();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					Thread.sleep(POLL_INTERVAL);
				} 
				catch (InterruptedException e) {
				}
			}
		}
	}
}
