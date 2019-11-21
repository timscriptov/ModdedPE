package com.microsoft.onlineid.userdata;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.log.Logger;

public class TelephonyManagerReader implements IPhoneNumberReader {
    private final TelephonyManager _telephonyManager;

    public TelephonyManagerReader(Context applicationContext) {
        this((TelephonyManager) applicationContext.getSystemService("phone"));
    }

    TelephonyManagerReader(TelephonyManager telephonyManager) {
        this._telephonyManager = telephonyManager;
    }

    public String getPhoneNumber() {
        String phoneNumber = null;
        try {
            phoneNumber = this._telephonyManager.getLine1Number();
        } catch (SecurityException e) {
            Logger.warning("Could not obtain phone number via getLine1Number(): " + e.getMessage());
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.MobilePhoneNumber, TextUtils.isEmpty(phoneNumber) ? ClientAnalytics.DoesntExistInTelephonyManager : ClientAnalytics.ExistsInTelephonyManager);
        return phoneNumber;
    }

    public String getIsoCountryCode() {
        String countryCode = null;
        try {
            countryCode = this._telephonyManager.getSimCountryIso();
        } catch (SecurityException e) {
            Logger.warning("Could not obtain country code via getSimCountryIso(): " + e.getMessage());
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.CountryCode, TextUtils.isEmpty(countryCode) ? ClientAnalytics.DoesntExistInTelephonyManager : ClientAnalytics.ExistsInTelephonyManager);
        return countryCode;
    }
}
