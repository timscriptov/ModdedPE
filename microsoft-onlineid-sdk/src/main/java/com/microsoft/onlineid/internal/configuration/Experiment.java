package com.microsoft.onlineid.internal.configuration;

import android.text.TextUtils;

public enum Experiment {
    QRCodeExperiment("MSAClient_Experiment1");
    
    private final String _experimentName;

    private Experiment(String experimentName) {
        this._experimentName = experimentName;
    }

    public String getName() {
        return this._experimentName;
    }

    public String toString() {
        return this._experimentName;
    }

    public static String getExperimentList() {
        return TextUtils.join(",", values());
    }
}
