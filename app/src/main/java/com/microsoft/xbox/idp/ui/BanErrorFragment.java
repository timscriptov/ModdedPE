package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.idp.compat.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class BanErrorFragment extends BaseFragment {
    public static final String ARG_GAMER_TAG = "ARG_GAMER_TAG";
    private static final String TAG = "BanErrorFragment";

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_ban, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            Log.e(TAG, "No arguments provided");
        } else if (args.containsKey(ARG_GAMER_TAG)) {
            String string = args.getString(ARG_GAMER_TAG);
        } else {
            Log.e(TAG, "No ARG_GAMER_TAG provided");
        }
    }
}