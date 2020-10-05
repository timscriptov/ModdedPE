package com.microsoft.xbox.idp.compat;

import android.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public abstract class BaseFragment extends Fragment {
    public static final String ARG_ALT_BUTTON_TEXT = "ARG_ALT_BUTTON_TEXT";
    public static final String ARG_LOG_IN_BUTTON_TEXT = "ARG_LOG_IN_BUTTON_TEXT";
    public static final String ARG_USER_PTR = "ARG_USER_PTR";

    public CharSequence getActivityTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            return null;
        }
        return activity.getTitle();
    }
}