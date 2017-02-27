package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Configuration Result
 */

class ConfigurationResult implements Parcelable {
    @SerializedName("enablePercent")
    private double enablePercent;
    @SerializedName("sendUrl")
    private String sendUrl;
    @SerializedName("sendHeaders")
    private Header header;

    private ConfigurationResult(Parcel in) {
        enablePercent = in.readDouble();
        sendUrl = in.readString();
        header = in.readParcelable(Header.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(enablePercent);
        dest.writeString(sendUrl);
        dest.writeParcelable(header, flags);
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

    public Header getHeader() {
        return header;
    }
}
