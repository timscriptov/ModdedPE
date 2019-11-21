package com.microsoft.onlineid.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.ArrayList;

public class ActivityResultSender {
    protected final Activity _activity;
    protected final Bundle _bundle = new Bundle();
    protected int _resultCode;

    public enum ResultType {
        Account("account"),
        Ticket("ticket");
        
        private final String _value;

        private ResultType(String value) {
            this._value = value;
        }

        public String getValue() {
            return this._value;
        }

        public static ResultType fromString(String str) {
            for (ResultType type : values()) {
                if (type.getValue().equals(str)) {
                    return type;
                }
            }
            return null;
        }
    }

    public ActivityResultSender(Activity activity, ResultType type) {
        this._activity = activity;
        this._bundle.putString(BundleMarshaller.ActivityResultTypeKey, type.getValue());
        this._bundle.putBundle(BundleMarshaller.ClientStateBundleKey, this._activity.getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey));
        this._resultCode = 0;
        set();
    }

    public ActivityResultSender putLimitedUserAccount(AuthenticatorUserAccount account) {
        this._bundle.putAll(BundleMarshaller.limitedUserAccountToBundle(account));
        this._resultCode = -1;
        return this;
    }

    public ActivityResultSender putTicket(Ticket ticket) {
        this._bundle.putAll(BundleMarshaller.ticketToBundle(ticket));
        this._resultCode = -1;
        return this;
    }

    public ActivityResultSender putSignedOutCid(String cid, boolean thisAppOnly) {
        this._bundle.putString(BundleMarshaller.UserCidKey, cid);
        this._bundle.putBoolean(BundleMarshaller.IsSignedOutOfThisAppOnlyKey, thisAppOnly);
        return putException(new AccountNotFoundException("The account was signed out."));
    }

    private ActivityResultSender putWebFlowTelemetryFields(ArrayList<String> events, boolean wereAllEventsCaptured) {
        this._bundle.putStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey, events);
        return putWereAllWebFlowTelemetryEventsCaptured(wereAllEventsCaptured);
    }

    public ActivityResultSender putWebFlowTelemetryFields(ApiResult apiResult) {
        return putWebFlowTelemetryFields(apiResult.getWebFlowTelemetryEvents(), apiResult.getWereAllWebFlowTelemetryEventsCaptured());
    }

    public ActivityResultSender putWereAllWebFlowTelemetryEventsCaptured(boolean wereAllEventsCaptured) {
        this._bundle.putBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, wereAllEventsCaptured);
        return this;
    }

    public ActivityResultSender putException(Exception e) {
        this._bundle.putAll(BundleMarshaller.exceptionToBundle(e));
        this._resultCode = -1;
        return this;
    }

    public void set() {
        this._activity.setResult(this._resultCode, new Intent().putExtras(this._bundle));
    }
}
