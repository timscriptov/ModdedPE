package com.microsoft.onlineid;

import android.os.Bundle;

public class SignUpOptions extends RequestOptions<SignUpOptions> {
    public SignUpOptions() {
        this(new Bundle());
    }

    public SignUpOptions(Bundle bundle) {
        super(bundle);
    }
}
