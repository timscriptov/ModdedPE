package com.microsoft.onlineid.sts;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.configuration.Flight;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class FlightManager {
    private static final Comparator<AuthenticatorUserAccount> PuidComparator = new Comparator<AuthenticatorUserAccount>() {
        public int compare(AuthenticatorUserAccount x, AuthenticatorUserAccount y) {
            return x.getPuid().compareTo(y.getPuid());
        }
    };
    private static int ResultTooManyFlights = -1;
    private AuthenticatorAccountManager _accountManager;
    private final Context _applicationContext;
    private final TypedStorage _typedStorage;

    public FlightManager(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._accountManager = new AuthenticatorAccountManager(this._applicationContext);
        this._typedStorage = new TypedStorage(this._applicationContext);
    }

    protected FlightManager() {
        this._applicationContext = null;
        this._typedStorage = null;
    }

    public Set<Integer> getFlights() {
        if (isDeviceFlightOverrideEnabled()) {
            Set<Integer> flights = this._typedStorage.readDeviceBasedFlights();
            if (flights != null) {
                return flights;
            }
            return Collections.emptySet();
        }
        AuthenticatorUserAccount primeAccount = getPrimeAccount();
        return primeAccount != null ? primeAccount.getFlights() : Collections.emptySet();
    }

    public void setFlights(Set<Integer> newSelectedFlights) {
        AuthenticatorUserAccount primeAccount = getPrimeAccount();
        if (isDeviceFlightOverrideEnabled()) {
            this._typedStorage.writeDeviceBasedFlights(newSelectedFlights);
        } else if (primeAccount != null) {
            primeAccount.setFlights(newSelectedFlights);
            this._typedStorage.writeAccount(primeAccount);
        }
    }

    private AuthenticatorUserAccount getPrimeAccount() {
        if (this._typedStorage.hasAccounts()) {
            return (AuthenticatorUserAccount) Collections.min(this._accountManager.getAccounts(), PuidComparator);
        }
        return null;
    }

    public boolean isDeviceFlightOverrideEnabled() {
        return this._typedStorage.readDeviceFlightOverrideEnabled();
    }

    public void setDeviceFlightOverrideEnabled(boolean shouldOverride) {
        this._typedStorage.writeDeviceFlightOverrideEnabled(shouldOverride);
    }

    public void enrollInFlights() {
        Set<Integer> flights = getFlights();
        Logger.info("Enrolling in Flights" + TextUtils.join(", ", flights));
        unenrollAllFlights();
        for (Integer intValue : flights) {
            int flightID = intValue.intValue();
            if (flightID != Flight.QRCode.getFlightID()) {
                if (flightID == ResultTooManyFlights) {
                    Logger.error("This client is in too many flights!  They are currently enrolled in " + TextUtils.join(", ", flights));
                } else {
                    Logger.warning("Unrecognized flight number " + flightID + " returned");
                }
            }
        }
    }

    public boolean isInNgcFlight() {
        return getFlights().contains(Integer.valueOf(Flight.QRCode.getFlightID()));
    }

    public boolean canShowNgc() {
        return isInNgcFlight() || new AuthenticatorAccountManager(this._applicationContext).hasNgcSessionApprovalAccounts();
    }

    private void unenrollAllFlights() {
    }
}
