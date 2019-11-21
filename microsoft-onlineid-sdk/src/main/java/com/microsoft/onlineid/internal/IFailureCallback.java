package com.microsoft.onlineid.internal;

import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;

public interface IFailureCallback {
    void onFailure(AuthenticationException authenticationException, Bundle bundle);
}
