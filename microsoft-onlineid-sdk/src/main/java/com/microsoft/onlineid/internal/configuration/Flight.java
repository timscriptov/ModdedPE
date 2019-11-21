package com.microsoft.onlineid.internal.configuration;

public enum Flight {
    QRCode(11, "qr_code");
    
    private final int _flightID;
    private final String _flightName;

    private Flight(int flightID, String flightName) {
        this._flightID = flightID;
        this._flightName = flightName;
    }

    public int getFlightID() {
        return this._flightID;
    }

    public String getFlightName() {
        return this._flightName;
    }
}
