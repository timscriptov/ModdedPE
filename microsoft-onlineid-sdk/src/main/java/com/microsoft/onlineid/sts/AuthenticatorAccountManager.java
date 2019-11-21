package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AuthenticatorAccountManager {
    private final Context _applicationContext;
    private final TypedStorage _typedStorage;

    public AuthenticatorAccountManager(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._typedStorage = new TypedStorage(applicationContext);
    }

    public AuthenticatorAccountManager(TypedStorage typedStorage) {
        this._applicationContext = null;
        this._typedStorage = typedStorage;
    }

    public boolean hasAccounts() {
        return this._typedStorage.hasAccounts();
    }

    public boolean hasSessionApprovalAccounts() {
        for (AuthenticatorUserAccount account : getAccounts()) {
            if (account.isSessionApprover()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNgcSessionApprovalAccounts() {
        for (AuthenticatorUserAccount account : getAccounts()) {
            if (account.hasNgcRegistrationSucceeded()) {
                return true;
            }
        }
        return false;
    }

    public Set<AuthenticatorUserAccount> getAccounts() {
        return this._typedStorage.readAllAccounts();
    }

    public Set<AuthenticatorUserAccount> getSessionApprovalAccounts() {
        Set<AuthenticatorUserAccount> accounts = new HashSet();
        for (AuthenticatorUserAccount account : getAccounts()) {
            if (account.isSessionApprover()) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    public Set<AuthenticatorUserAccount> getFilteredAccounts(Set<String> excludedAccounts) {
        Set<AuthenticatorUserAccount> accounts = this._typedStorage.readAllAccounts();
        if (!(excludedAccounts == null || excludedAccounts.isEmpty())) {
            Iterator<AuthenticatorUserAccount> iterator = accounts.iterator();
            while (iterator.hasNext()) {
                if (excludedAccounts.contains(((AuthenticatorUserAccount) iterator.next()).getCid())) {
                    iterator.remove();
                }
            }
        }
        return accounts;
    }

    public AuthenticatorUserAccount getAccountByCid(String cid) {
        for (AuthenticatorUserAccount account : this._typedStorage.readAllAccounts()) {
            if (cid.equalsIgnoreCase(account.getCid())) {
                return account;
            }
        }
        return null;
    }

    public AuthenticatorUserAccount getAccountByPuid(String puid) {
        return this._typedStorage.readAccount(puid);
    }

    void removeLastSavedUserTileImage(AuthenticatorUserAccount account) {
        File file = this._applicationContext.getFileStreamPath(account.getPuid() + DownloadProfileImageTask.UserTileExtension);
        if (file.exists()) {
            file.delete();
        }
    }
}
