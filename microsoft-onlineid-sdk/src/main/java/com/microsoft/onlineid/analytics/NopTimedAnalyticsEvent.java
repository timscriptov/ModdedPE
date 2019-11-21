package com.microsoft.onlineid.analytics;

public class NopTimedAnalyticsEvent implements ITimedAnalyticsEvent {
    public NopTimedAnalyticsEvent setLabel(String label) {
        return this;
    }

    public NopTimedAnalyticsEvent start() {
        return this;
    }

    public void end() {
    }
}
