package jp.co.rakuten.sdtd.perf.runtime.internal;

/**
 * Configuration Parameters
 */
class ConfigurationParam {
    private String platform;
    private String appId;
    private String appVersion;
    private String sdkVersion;
    private String countryCode;

    private ConfigurationParam(Builder builder) {
        platform = builder.platform;
        appId = builder.appId;
        appVersion = builder.appVersion;
        sdkVersion = builder.sdkVersion;
        countryCode = builder.countryCode;
    }

    static class Builder {
        private String platform;
        private String appId;
        private String appVersion;
        private String sdkVersion;
        private String countryCode;

        Builder setPlatform(String platform) {
            this.platform = platform;
            return this;
        }

        Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        Builder setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }

        Builder setCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        ConfigurationParam build() {
            if (platform == null) throw new IllegalStateException("Platform cannot be null");
            if (appId == null) throw new IllegalStateException("App Id cannot be null");
            if (appVersion == null) throw new IllegalStateException("App Version cannot be null");
            if (sdkVersion == null) throw new IllegalStateException("Sdk Version cannot be null");
            if (countryCode == null) throw new IllegalStateException("Country Code cannot be null");
            return new ConfigurationParam(this);
        }
    }

    public String getPlatform() {
        return platform;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
