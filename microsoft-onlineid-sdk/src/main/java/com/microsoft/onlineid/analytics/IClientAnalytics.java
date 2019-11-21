package com.microsoft.onlineid.analytics;

import java.util.Map;

public interface IClientAnalytics {
    ITimedAnalyticsEvent createTimedEvent(String str, String str2);

    ITimedAnalyticsEvent createTimedEvent(String str, String str2, String str3);

    IClientAnalytics logCertificates(Map<String, byte[]> map);

    IClientAnalytics logClockSkew(long j);

    IClientAnalytics logEvent(String str, String str2);

    IClientAnalytics logEvent(String str, String str2, String str3);

    IClientAnalytics logEvent(String str, String str2, String str3, Long l);

    IClientAnalytics logException(Throwable th);

    IClientAnalytics logScreenView(String str);

    IClientAnalytics logTotalAccountsEvent(String str, int i, int i2);

    IClientAnalytics send(Map<String, String> map);

    void setTestMode();
}
