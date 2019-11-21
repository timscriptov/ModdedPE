package com.microsoft.onlineid.internal.sso.client;

import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.IFailureCallback;
import com.microsoft.onlineid.internal.log.Logger;

public abstract class SsoRunnable implements Runnable {
    private final IFailureCallback _failureCallback;
    private final Bundle _state;

    public abstract void performRequest() throws AuthenticationException;

    public SsoRunnable(IFailureCallback failureCallback, Bundle state) {
        this._failureCallback = failureCallback;
        this._state = state;
    }

    public void run() {
        try {
            performRequest();
        } catch (AuthenticationException e) {
            Logger.error(e.toString());
            this._failureCallback.onFailure(e, this._state);
        } catch (Throwable e2) {
            InternalException ie = new InternalException(e2);
            Logger.error(ie.toString());
            this._failureCallback.onFailure(ie, this._state);
        }
    }
}
