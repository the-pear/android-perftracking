package jp.co.rakuten.sdtd.perf.core;

import java.util.Locale;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class EnvironmentInfo {
	public String device;
	public String country;
	public String network;
	
	public static EnvironmentInfo get(Context context) {
		EnvironmentInfo info = new EnvironmentInfo();
		
		info.device = Build.MODEL;
		
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null)
		{
			info.country = tm.getSimCountryIso();
			info.network = tm.getNetworkOperatorName();
		}
		
		if (info.country == null)
		{
			Locale.getDefault().getCountry();
		}
		
		return info;
	}
}
