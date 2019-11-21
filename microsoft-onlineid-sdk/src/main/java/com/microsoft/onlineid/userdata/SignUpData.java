package com.microsoft.onlineid.userdata;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.userdata.MeContactReader.FullName;

public class SignUpData {
    private final FullName _fullName;
    private final MeContactReader _meContactReader;
    private final TelephonyManagerReader _telephonyManagerReader;

    public SignUpData(Context applicationContext) {
        this(new TelephonyManagerReader(applicationContext), new MeContactReader(applicationContext));
    }

    SignUpData(TelephonyManagerReader telephonyManagerReader, MeContactReader meContactReader) {
        this._telephonyManagerReader = telephonyManagerReader;
        this._meContactReader = meContactReader;
        this._fullName = this._meContactReader.getFullName();
        Assertion.check(this._fullName != null);
    }

    public String getFirstName() {
        return this._fullName.getFirstName();
    }

    public String getLastName() {
        return this._fullName.getLastName();
    }

    public String getPhone() {
        String phoneNumber = this._telephonyManagerReader.getPhoneNumber();
        return TextUtils.isEmpty(phoneNumber) ? this._meContactReader.getPhoneNumber() : phoneNumber;
    }

    public String getCountryCode() {
        return this._telephonyManagerReader.getIsoCountryCode();
    }
}
