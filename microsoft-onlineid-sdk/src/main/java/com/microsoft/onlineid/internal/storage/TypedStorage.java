package com.microsoft.onlineid.internal.storage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.IClientAnalytics;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.BundleMarshallerException;
import com.microsoft.onlineid.internal.storage.Storage.Editor;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DeviceIdentity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TypedStorage {
    protected static final String AccountKeyToken = "Account";
    protected static final String AccountsCollectionKey = "Accounts";
    protected static final String ClockSkewKey = "ClockSkew";
    protected static final Object CollectionLock = new Object();
    protected static final String ConfigLastDownloadedTimeKey = "ConfigLastDownloadedTime";
    protected static final String DeviceBasedFlightsKey = "DeviceBasedFlights";
    protected static final String DeviceFlightOverrideKey = "DeviceFlightOverride";
    protected static final String DeviceIdentityKey = "Device";
    protected static final String FormatSeparator = "|";
    protected static final String LastBackupPushedTimeKey = "LastBackupPushedTime";
    protected static final String LastBackupReceivedTimeKey = "LastBackupReceivedTime";
    protected static final String SdkVersionKey = "SdkVersion";
    protected static final String TicketCollectionKeyToken = "Tickets";
    protected static final String TicketKeyToken = "Ticket";
    protected final Storage _storage;

    public TypedStorage(Context applicationContext) {
        Objects.verifyArgumentNotNull(applicationContext, "applicationContext");
        this._storage = new Storage(applicationContext);
    }

    protected TypedStorage(Storage storage) {
        this._storage = storage;
    }

    public DeviceIdentity readDeviceIdentity() {
        return (DeviceIdentity) this._storage.readObject(DeviceIdentityKey, getDeviceIdentitySerializer());
    }

    public void writeDeviceIdentity(DeviceIdentity identity) {
        this._storage.edit().writeObject(DeviceIdentityKey, identity, getDeviceIdentitySerializer()).apply();
    }

    public void deleteDeviceIdentity() {
        this._storage.edit().remove(DeviceIdentityKey).apply();
    }

    protected void storeTicket(String accountID, String appID, Ticket ticket) {
        writeToCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, ticket.getScope()), ticket, getTicketSerializer());
    }

    protected void removeTicket(String accountID, String appID, ISecurityScope scope) {
        removeFromCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, scope));
    }

    protected void removeTickets(String accountID) {
        removeCollection(constructTicketCollectionKey(accountID));
    }

    private boolean hasTickets(String accountID) {
        return hasCollection(constructTicketCollectionKey(accountID));
    }

    protected Ticket getTicket(String accountID, String appID, ISecurityScope securityScope) {
        return (Ticket) readFromCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, securityScope), getTicketSerializer());
    }

    protected <T> T readFromCollection(String collectionKey, String valueKey, ISerializer<T> serializer) {
        String encoded = this._storage.readString(valueKey);
        T result = null;
        if (encoded != null) {
            try {
                result = serializer.deserialize(encoded);
                if (result == null) {
                    removeFromCollection(collectionKey, valueKey);
                }
            } catch (IOException e) {
                Logger.warning(String.format(Locale.US, "Value in storage at '%s' was corrupt.", new Object[]{valueKey}));
                if (result == null) {
                    removeFromCollection(collectionKey, valueKey);
                }
            } catch (Throwable th) {
                if (result == null) {
                    removeFromCollection(collectionKey, valueKey);
                }
            }
        }
        return result;
    }

    public void writeClockSkew(long skew) {
        this._storage.edit().writeLong(ClockSkewKey, skew).apply();
    }

    public long readClockSkew() {
        return this._storage.readLong(ClockSkewKey, 0);
    }

    public boolean clearSynchronous() {
        boolean commit;
        synchronized (CollectionLock) {
            commit = this._storage.edit().clear().commit();
        }
        return commit;
    }

    public AuthenticatorUserAccount readAccount(String accountPuid) {
        return (AuthenticatorUserAccount) readFromCollection(AccountsCollectionKey, constructAccountKey(accountPuid), getAccountSerializer());
    }

    public void writeAccount(AuthenticatorUserAccount account) {
        Strings.verifyArgumentNotNullOrEmpty(account.getPuid(), "account.PUID");
        writeToCollection(AccountsCollectionKey, constructAccountKey(account.getPuid()), account, getAccountSerializer());
    }

    public void removeAccount(String accountPuid) {
        removeFromCollection(AccountsCollectionKey, constructAccountKey(accountPuid));
        removeTickets(accountPuid);
    }

    public boolean hasAccounts() {
        return hasCollection(AccountsCollectionKey);
    }

    public Set<AuthenticatorUserAccount> readAllAccounts() {
        return readCollection(AccountsCollectionKey, getAccountSerializer());
    }

    public Bundle retrieveBackup() {
        Bundle backup = new Bundle();
        DeviceIdentity deviceIdentity = readDeviceIdentity();
        if (deviceIdentity != null) {
            backup.putBundle(BundleMarshaller.BackupDeviceKey, BundleMarshaller.deviceAccountToBundle(deviceIdentity));
        }
        ArrayList<Bundle> accountsBundles = new ArrayList();
        for (AuthenticatorUserAccount account : readAllAccounts()) {
            accountsBundles.add(BundleMarshaller.userAccountToBundle(account));
            if (deviceIdentity == null) {
                Assertion.check(!hasTickets(account.getPuid()));
            }
        }
        if (!accountsBundles.isEmpty()) {
            backup.putParcelableArrayList(BundleMarshaller.BackupUsersKey, accountsBundles);
        }
        return backup;
    }

    public void storeBackup(Bundle backup) throws BundleMarshallerException {
        Bundle deviceBundle = backup.getBundle(BundleMarshaller.BackupDeviceKey);
        String serializedDeviceIdentity = null;
        if (deviceBundle != null) {
            try {
                serializedDeviceIdentity = getDeviceIdentitySerializer().serialize(BundleMarshaller.deviceAccountFromBundle(deviceBundle));
            } catch (Throwable e) {
                throw new StorageException(e);
            }
        }
        List<Bundle> accountsBundles = backup.getParcelableArrayList(BundleMarshaller.BackupUsersKey);
        Map serializedAccounts = new HashMap();
        ISerializer<AuthenticatorUserAccount> accountSerializer = getAccountSerializer();
        if (accountsBundles != null) {
            for (Bundle accountBundle : accountsBundles) {
                try {
                    AuthenticatorUserAccount account = BundleMarshaller.userAccountFromBundle(accountBundle);
                    serializedAccounts.put(constructAccountKey(account.getPuid()), accountSerializer.serialize(account));
                } catch (Throwable e2) {
                    throw new StorageException(e2);
                } catch (BundleMarshallerException e3) {
                    Logger.error("Encountered an error while trying to unbundle accounts.", e3);
                    ClientAnalytics.get().logException(e3);
                }
            }
        }
        synchronized (CollectionLock) {
            Editor editor = this._storage.edit();
            if (serializedDeviceIdentity != null) {
                editor.writeString(DeviceIdentityKey, serializedDeviceIdentity);
            }
            for (String accountKey : this._storage.readStringSet(AccountsCollectionKey)) {
                replaceCollection(constructTicketCollectionKeyFromAccountKey(accountKey), null, editor);
            }
            replaceCollection(AccountsCollectionKey, serializedAccounts, editor);
            editor.writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis());
            editor.apply();
        }
    }

    public void writeLastBackupPushedTime() {
        this._storage.edit().writeLong(LastBackupPushedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readLastBackupPushedTime() {
        return this._storage.readLong(LastBackupPushedTimeKey, 0);
    }

    public void writeLastBackupReceivedTime() {
        this._storage.edit().writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readLastBackupReceivedTime() {
        return this._storage.readLong(LastBackupReceivedTimeKey, 0);
    }

    public void writeConfigLastDownloadedTime() {
        this._storage.edit().writeLong(ConfigLastDownloadedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readConfigLastDownloadedTime() {
        return this._storage.readLong(ConfigLastDownloadedTimeKey, 0);
    }

    public void writeDeviceBasedFlights(Set<Integer> deviceFlights) {
        HashSet<String> deviceFlightsString = new HashSet(deviceFlights.size());
        for (Integer deviceFlight : deviceFlights) {
            deviceFlightsString.add(deviceFlight.toString());
        }
        this._storage.edit().writeStringSet(DeviceBasedFlightsKey, deviceFlightsString).apply();
    }

    public Set<Integer> readDeviceBasedFlights() {
        Set<String> deviceFlightsString = this._storage.readStringSet(DeviceBasedFlightsKey);
        HashSet<Integer> deviceFlights = new HashSet(deviceFlightsString.size());
        for (String deviceFlight : deviceFlightsString) {
            deviceFlights.add(Integer.valueOf(Integer.parseInt(deviceFlight)));
        }
        return deviceFlights;
    }

    public void writeDeviceFlightOverrideEnabled(boolean shouldOverride) {
        this._storage.edit().writeBoolean(DeviceFlightOverrideKey, shouldOverride).apply();
    }

    public boolean readDeviceFlightOverrideEnabled() {
        return this._storage.readBoolean(DeviceFlightOverrideKey, false);
    }

    public void writeSdkVersion(String version) {
        this._storage.edit().writeString(SdkVersionKey, version).apply();
    }

    public String readSdkVersion() {
        return this._storage.readString(SdkVersionKey);
    }

    protected <T> void writeToCollection(String collectionKey, String valueKey, T value, ISerializer<T> serializer) {
        Assertion.check(value != null, "Attempted to write null value to collection.");
        try {
            String encoded = serializer.serialize(value);
            synchronized (CollectionLock) {
                Set<String> keys = this._storage.readStringSet(collectionKey);
                Editor editor = this._storage.edit();
                if (!keys.contains(valueKey)) {
                    Set<String> keys2 = new HashSet(keys);
                    keys2.add(valueKey);
                    editor.writeStringSet(collectionKey, keys2);
                    keys = keys2;
                }
                editor.writeString(valueKey, encoded).apply();
            }
        } catch (Throwable e) {
            throw new StorageException(e);
        }
    }

    protected void removeFromCollection(String collectionKey, String... valueKeys) {
        removeFromCollection(collectionKey, Arrays.asList(valueKeys));
    }

    protected void removeFromCollection(String collectionKey, Collection<String> valueKeys) {
        if (!valueKeys.isEmpty()) {
            Editor editor = this._storage.edit();
            for (String key : valueKeys) {
                editor.remove(key);
            }
            synchronized (CollectionLock) {
                Set<String> keys = new HashSet(this._storage.readStringSet(collectionKey));
                keys.removeAll(valueKeys);
                if (keys.isEmpty()) {
                    editor.remove(collectionKey);
                } else {
                    editor.writeStringSet(collectionKey, keys);
                }
                editor.apply();
            }
        }
    }

    protected boolean hasCollection(String collectionKey) {
        boolean z;
        synchronized (CollectionLock) {
            z = !this._storage.readStringSet(collectionKey).isEmpty();
        }
        return z;
    }

    protected <T> Set<T> readCollection(String collectionKey, ISerializer<T> serializer) {
        Map<String, String> serializedValues = new HashMap();
        synchronized (CollectionLock) {
            Set<String> keys = this._storage.readStringSet(collectionKey);
            Set<String> retainedKeys = new HashSet(keys);
            for (String key : keys) {
                String value = this._storage.readString(key);
                if (value != null) {
                    serializedValues.put(key, value);
                } else {
                    Assertion.check(false, "Stored collection value was null.");
                    retainedKeys.remove(key);
                }
            }
            if (retainedKeys.size() != keys.size()) {
                String substring;
                Logger.error("Key set was out of sync for collection: " + collectionKey);
                int index = collectionKey.indexOf(FormatSeparator);
                IClientAnalytics iClientAnalytics = ClientAnalytics.get();
                String str = ClientAnalytics.StorageCategory;
                String str2 = ClientAnalytics.CollectionConsistencyError;
                if (index > 0) {
                    substring = collectionKey.substring(0, index);
                } else {
                    substring = collectionKey;
                }
                iClientAnalytics.logEvent(str, str2, substring);
                this._storage.edit().writeStringSet(collectionKey, retainedKeys).apply();
            }
        }
        Set<T> result = Collections.emptySet();
        try {
            result = serializer.deserializeAll(serializedValues);
        } catch (IOException e) {
            Logger.error("Unable to deserialize indexed collection.", e);
        }
        return result;
    }

    protected <T> void replaceCollection(String collectionKey, Map<String, T> values, ISerializer<T> serializer) {
        try {
            Map encoded = serializer.serializeAll(values);
            synchronized (CollectionLock) {
                Editor editor = this._storage.edit();
                replaceCollection(collectionKey, encoded, editor);
                editor.apply();
            }
        } catch (Throwable e) {
            throw new StorageException(e);
        }
    }

    private <T> void replaceCollection(String collectionKey, Map<String, String> values, Editor editor) {
        for (String key : this._storage.readStringSet(collectionKey)) {
            editor.remove(key);
        }
        if (values != null) {
            for (Entry<String, String> entry : values.entrySet()) {
                editor.writeString((String) entry.getKey(), (String) entry.getValue());
            }
            editor.writeStringSet(collectionKey, values.keySet());
            return;
        }
        editor.remove(collectionKey);
    }

    protected void removeCollection(String collectionKey) {
        synchronized (CollectionLock) {
            Editor editor = this._storage.edit();
            replaceCollection(collectionKey, null, editor);
            editor.apply();
        }
    }

    public void blockForCommit() {
        this._storage.blockForCommit();
    }

    public void dumpContents() {
        this._storage.dumpContents();
    }

    protected ISerializer<AuthenticatorUserAccount> getAccountSerializer() {
        return new ObjectStreamSerializer();
    }

    protected ISerializer<DeviceIdentity> getDeviceIdentitySerializer() {
        return new ObjectStreamSerializer();
    }

    protected ISerializer<Ticket> getTicketSerializer() {
        return new ObjectStreamSerializer();
    }

    protected static String constructKey(Object... tokens) {
        return TextUtils.join(FormatSeparator, tokens);
    }

    protected static String constructAccountKey(String accountPuid) {
        return constructKey(AccountKeyToken, accountPuid.toLowerCase(Locale.US));
    }

    protected static String constructTicketKey(String accountPuid, String appID, ISecurityScope securityScope) {
        Objects.verifyArgumentNotNull(securityScope.getTarget(), "Ticket target");
        Objects.verifyArgumentNotNull(securityScope.getPolicy(), "Ticket policy");
        return constructKey(TicketKeyToken, accountPuid.toLowerCase(Locale.US), appID.toLowerCase(Locale.US), securityScope.getTarget().toLowerCase(Locale.US), securityScope.getPolicy().toLowerCase(Locale.US));
    }

    protected static String constructTicketCollectionKey(String accountPuid) {
        return constructKey(TicketCollectionKeyToken, accountPuid.toLowerCase(Locale.US));
    }

    protected static String constructTicketCollectionKeyFromAccountKey(String accountKey) {
        return accountKey.replace(AccountKeyToken, TicketCollectionKeyToken);
    }
}
