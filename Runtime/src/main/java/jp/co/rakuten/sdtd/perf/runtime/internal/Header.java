package jp.co.rakuten.sdtd.perf.runtime.internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Header
 */

class Header implements Parcelable {
    private String authorization;
    private String contentType;
    private String brokerProperties;

    private Header(Parcel in) {
        authorization = in.readString();
        contentType = in.readString();
        brokerProperties = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorization);
        dest.writeString(contentType);
        dest.writeString(brokerProperties);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Header> CREATOR = new Creator<Header>() {
        @Override
        public Header createFromParcel(Parcel in) {
            return new Header(in);
        }

        @Override
        public Header[] newArray(int size) {
            return new Header[size];
        }
    };

    public String getAuthorization() {
        return authorization;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBrokerProperties() {
        return brokerProperties;
    }
}
