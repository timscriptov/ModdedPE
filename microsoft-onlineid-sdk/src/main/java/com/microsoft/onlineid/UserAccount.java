package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class UserAccount {
    private final AccountManager _accountManager;
    private final String _cid;
    private final String _puid;
    private final String _username;

    UserAccount(AccountManager manager, String cid, String puid, String username) {
        this._accountManager = manager;
        this._cid = cid;
        this._puid = puid;
        this._username = username;
    }

    UserAccount(AccountManager manager, AuthenticatorUserAccount account) {
        this(manager, account.getCid(), account.getPuid(), account.getUsername());
    }

    public void getTicket(ISecurityScope scope, Bundle state) {
        this._accountManager.getTicket(this, scope, state);
    }

    public String getCid() {
        return this._cid;
    }

    public String getUsername() {
        return this._username;
    }

    String getPuid() {
        return this._puid;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof UserAccount)) {
            return false;
        }
        UserAccount other = (UserAccount) o;
        if (Objects.equals(this._puid, other._puid) && Objects.equals(this._cid, other._cid)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this._puid) + Objects.hashCode(this._cid);
    }
}
