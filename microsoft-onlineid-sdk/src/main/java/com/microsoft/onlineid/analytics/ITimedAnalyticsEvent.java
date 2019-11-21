package com.microsoft.onlineid.analytics;

public interface ITimedAnalyticsEvent {
    void end();

    ITimedAnalyticsEvent setLabel(String str);

    ITimedAnalyticsEvent start();
}
