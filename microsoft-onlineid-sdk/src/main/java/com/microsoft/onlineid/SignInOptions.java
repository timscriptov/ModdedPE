package com.microsoft.onlineid;

import android.os.Bundle;

public class SignInOptions extends RequestOptions<SignInOptions> {
    public SignInOptions() {
        this(new Bundle());
    }

    public SignInOptions(Bundle bundle) {
        super(bundle);
    }
}
