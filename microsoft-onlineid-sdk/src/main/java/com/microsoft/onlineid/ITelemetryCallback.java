package com.microsoft.onlineid;

public interface ITelemetryCallback {
    void webTelemetryEventsReceived(Iterable<String> iterable, boolean z);
}
