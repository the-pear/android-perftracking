package jp.co.rakuten.sdtd.perf.stubs.android.content;

public abstract class Context {
	public static final String TELEPHONY_SERVICE = null;
	
    public void startActivity(Intent intent) {
    }
    
    public abstract Object getSystemService(String name);
}
