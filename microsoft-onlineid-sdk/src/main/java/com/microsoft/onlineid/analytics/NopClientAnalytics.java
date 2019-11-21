package com.microsoft.onlineid.analytics;

import java.util.Map;

public class NopClientAnalytics implements IClientAnalytics {
    public void setTestMode() {
    }

    public IClientAnalytics send(Map<String, String> map) {
        return this;
    }

    public IClientAnalytics logScreenView(String screenName) {
        return this;
    }

    public IClientAnalytics logEvent(String category, String action) {
        return this;
    }

    public IClientAnalytics logEvent(String category, String action, String label) {
        return this;
    }

    public IClientAnalytics logEvent(String category, String action, String label, Long value) {
        return this;
    }

    public IClientAnalytics logTotalAccountsEvent(String category, int oldAccountCount, int newAccountCount) {
        return this;
    }

    public ITimedAnalyticsEvent createTimedEvent(String category, String name, String label) {
        return new NopTimedAnalyticsEvent();
    }

    public ITimedAnalyticsEvent createTimedEvent(String category, String name) {
        return new NopTimedAnalyticsEvent();
    }

    public IClientAnalytics logException(Throwable throwable) {
        return this;
    }

    public IClientAnalytics logClockSkew(long skew) {
        return this;
    }

    public IClientAnalytics logCertificates(Map<String, byte[]> map) {
        return this;
    }
}
