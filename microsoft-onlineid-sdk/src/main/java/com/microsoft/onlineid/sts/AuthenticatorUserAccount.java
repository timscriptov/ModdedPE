package com.microsoft.onlineid.sts;

import android.text.TextUtils;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthenticatorUserAccount implements Serializable {
    private static final long serialVersionUID = 1;
    private final String _cid;
    private String _displayName;
    private Set<Integer> _flights;
    private String _gcmRegistrationID;
    private boolean _isSessionApprover = false;
    private boolean _isSessionApproverRegistrationNeeded = true;
    private final String _puid;
    private String _serverKeyIdentifier;
    private long _timeOfLastProfileUpdate;
    private DAToken _token;
    private byte[] _totpKey;
    private String _username;

    public AuthenticatorUserAccount(String puid, String cid, String username, DAToken token) {
        Strings.verifyArgumentNotNullOrEmpty(username, AppProperties.UsernameKey);
        this._puid = puid;
        this._cid = cid;
        this._username = username;
        this._token = token;
        this._totpKey = null;
        this._serverKeyIdentifier = null;
        this._flights = new HashSet();
    }

    public boolean isNewAndInOutOfBandInterrupt() {
        return TextUtils.isEmpty(this._puid);
    }

    public String getPuid() {
        return this._puid;
    }

    public String getUsername() {
        return this._username;
    }

    public void setUsername(String username) {
        Strings.verifyArgumentNotNullOrEmpty(username, AppProperties.UsernameKey);
        this._username = username;
    }

    public String getCid() {
        return this._cid;
    }

    public boolean isSessionApprover() {
        return this._isSessionApprover;
    }

    public void setIsSessionApprover(boolean isSessionApprover) {
        this._isSessionApprover = isSessionApprover;
    }

    public boolean isSessionApproverRegistrationNeeded() {
        return this._isSessionApproverRegistrationNeeded;
    }

    public void setIsSessionApproverRegistrationNeeded(boolean isSessionApproverRegistrationNeeded) {
        this._isSessionApproverRegistrationNeeded = isSessionApproverRegistrationNeeded;
    }

    public boolean hasNgcRegistrationSucceeded() {
        return this._serverKeyIdentifier != null;
    }

    public Set<Integer> getFlights() {
        return this._flights != null ? this._flights : Collections.emptySet();
    }

    public void setFlights(Set<Integer> flights) {
        this._flights = flights;
    }

    public DAToken getDAToken() {
        return this._token;
    }

    public void setDAToken(DAToken token) {
        Objects.verifyArgumentNotNull(token, "token");
        this._token = token;
    }

    public byte[] getTotpKey() {
        return this._totpKey;
    }

    public void setTotpKey(byte[] totpKey) {
        this._totpKey = totpKey;
    }

    public String getDisplayName() {
        return this._displayName;
    }

    public void setDisplayName(String displayName) {
        this._displayName = displayName;
    }

    public long getTimeOfLastProfileUpdate() {
        return this._timeOfLastProfileUpdate;
    }

    public void setTimeOfLastProfileUpdate(long time) {
        this._timeOfLastProfileUpdate = time;
    }

    public String getGcmRegistrationID() {
        return this._gcmRegistrationID;
    }

    public void setGcmRegistrationID(String registrationID) {
        this._gcmRegistrationID = registrationID;
    }

    public void setServerKeyIdentifier(String keyIdentifier) {
        this._serverKeyIdentifier = keyIdentifier;
    }

    public String getServerKeyIdentifier() {
        return this._serverKeyIdentifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof AuthenticatorUserAccount)) {
            return false;
        }
        AuthenticatorUserAccount account = (AuthenticatorUserAccount) o;
        if (Objects.equals(this._puid, account._puid) && this._isSessionApprover == account._isSessionApprover && Objects.equals(this._username, account._username) && Objects.equals(this._token, account._token) && Arrays.equals(this._totpKey, account._totpKey)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this._puid);
    }
}
