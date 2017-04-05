package com.rakuten.tech.mobile.perf.rewriter;

public class Log {
	public static final int NONE = 0;
	public static final int ERROR = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	
	public int level;

	public void debug(String message) {
		if (level >= DEBUG) {
			System.out.println(message);
		}
	}
	
	public void info(String message) {
		if (level >= INFO) {
			System.out.println(message);
		}
	}
	
	public void error(String message) {
		if (level >= ERROR) {
			System.out.println("ERROR: " + message);
		}
	}
	
	public void error(String message, Throwable e) {
		if (level >= ERROR) {
			System.out.println("ERROR: " + message);
			e.printStackTrace();
		}
	}
}
