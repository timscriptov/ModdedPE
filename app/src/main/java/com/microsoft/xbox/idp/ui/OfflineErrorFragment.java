package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
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

public class OfflineErrorFragment extends BaseFragment {
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_offline, container, false);
    }
}
