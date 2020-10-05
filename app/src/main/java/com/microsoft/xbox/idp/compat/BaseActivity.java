package com.microsoft.xbox.idp.compat;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class BaseActivity extends AppCompatActivity {
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

    @SuppressLint("WrongConstant")
    public void setOrientation() {
        if ((getApplicationContext().getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
    }
}