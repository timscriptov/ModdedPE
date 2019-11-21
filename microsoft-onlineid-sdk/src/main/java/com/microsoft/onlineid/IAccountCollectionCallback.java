package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.IFailureCallback;
import java.util.Set;

public interface IAccountCollectionCallback extends IFailureCallback {
    void onAccountCollectionAcquired(Set<UserAccount> set, Bundle bundle);

    void onFailure(AuthenticationException authenticationException, Bundle bundle);
}
