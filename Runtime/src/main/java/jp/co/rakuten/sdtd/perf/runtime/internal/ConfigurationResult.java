package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Result
 */

class ConfigurationResult implements Parcelable {
    @SerializedName("enablePercent")
    private double enablePercent;
    @SerializedName("sendUrl")
    private String sendUrl;
    @SerializedName("sendHeaders")
    private Map<String, String> header;

    private ConfigurationResult(Parcel in) {
        enablePercent = in.readDouble();
        sendUrl = in.readString();
        Bundle bundle = in.readBundle();
        header = new HashMap<>();
        for(String key : bundle.keySet())
            header.put(key, bundle.getString(key));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(enablePercent);
        dest.writeString(sendUrl);
        Bundle bundle = new Bundle();
        for(String key : header.keySet())
            bundle.putString(key, header.get(key));
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConfigurationResult> CREATOR = new Creator<ConfigurationResult>() {
        @Override
        public ConfigurationResult createFromParcel(Parcel in) {
            return new ConfigurationResult(in);
        }

        @Override
        public ConfigurationResult[] newArray(int size) {
            return new ConfigurationResult[size];
        }
    };

    public double getEnablePercent() {
        return enablePercent;
    }

    public String getSendUrl() {
        return sendUrl;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    private void write(Parcel dest, Map<String, String> strings) {
        if (strings == null) {
            dest.writeInt(-1);
        }
        {
            dest.writeInt(strings.keySet().size());
            for (String key : strings.keySet()) {
                dest.writeString(key);
                dest.writeString(strings.get(key));
            }
        }
    }

    private Map<String, String> readStringMap(Parcel source) {
        int numKeys = source.readInt();
        if (numKeys == -1) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < numKeys; i++) {
            String key = source.readString();
            String value = source.readString();
            map.put(key, value);
        }
        return map;
    }
}
