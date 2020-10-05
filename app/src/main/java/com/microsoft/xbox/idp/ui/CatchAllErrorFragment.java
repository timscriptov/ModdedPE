package com.microsoft.xbox.idp.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.Const;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CatchAllErrorFragment extends BaseFragment {
    public static final String TAG = "CatchAllErrorFragment";

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_catch_all, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UiUtil.ensureClickableSpanOnUnderlineSpan(view.findViewById(R.id.xbid_error_message), R.string.xbid_catchall_error_android, new ClickableSpan() {
            public void onClick(View widget) {
                Log.d(CatchAllErrorFragment.TAG, "onClick");
                try {
                    startActivity(new Intent("android.intent.action.VIEW", Const.URL_XBOX_COM));
                } catch (ActivityNotFoundException e) {
                    Log.e(CatchAllErrorFragment.TAG, e.getMessage());
                }
            }
        });
    }
}
