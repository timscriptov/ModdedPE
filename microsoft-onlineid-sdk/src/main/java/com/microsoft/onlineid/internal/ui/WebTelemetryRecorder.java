package com.microsoft.onlineid.internal.ui;

import android.os.Bundle;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import java.util.ArrayList;
import java.util.Iterator;

public class WebTelemetryRecorder {
    private static final int MAX_CHAR_COUNT = 15000;
    private int _charCount = 0;
    private ArrayList<String> _events;
    private boolean _shouldRecord;
    private boolean _wereAllEventsCaptured;

    public WebTelemetryRecorder(boolean shouldRecord, Bundle savedInstanceState) {
        this._shouldRecord = shouldRecord;
        if (savedInstanceState == null || !savedInstanceState.containsKey(BundleMarshaller.WebFlowTelemetryEventsKey)) {
            this._events = new ArrayList();
            this._wereAllEventsCaptured = true;
            return;
        }
        this._events = savedInstanceState.getStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey);
        this._wereAllEventsCaptured = savedInstanceState.getBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, false);
        Iterator it = this._events.iterator();
        while (it.hasNext()) {
            this._charCount += ((String) it.next()).length();
        }
    }

    public void saveInstanceState(Bundle outState) {
        outState.putStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey, getEvents());
        outState.putBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, wereAllEventsCaptured());
    }

    public void recordEvent(String event) {
        if (!this._shouldRecord) {
            return;
        }
        if (canFitEvent(event)) {
            this._events.add(event);
            this._charCount += event.length();
            return;
        }
        this._wereAllEventsCaptured = false;
        Logger.warning("Dropped web telemetry event of size: " + event.length());
    }

    public boolean isRequested() {
        return this._shouldRecord;
    }

    private boolean canFitEvent(String event) {
        return this._charCount + event.length() <= MAX_CHAR_COUNT;
    }

    public boolean hasEvents() {
        return this._shouldRecord && !this._events.isEmpty();
    }

    public boolean wereAllEventsCaptured() {
        return this._wereAllEventsCaptured;
    }

    public ArrayList<String> getEvents() {
        return this._events;
    }
}
