package com.microsoft.xbox.idp.compat;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
    public boolean hasFragment(int fragmentId) {
        return getFragmentManager().findFragmentById(fragmentId) != null;
    }

    public void addFragment(int fragmentId, BaseFragment fragment) {
        getFragmentManager().beginTransaction().add(fragmentId, fragment).commit();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientation();
    }

    public void setOrientation() {
        if ((getApplicationContext().getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
    }
}
